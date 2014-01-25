package edu.kit.pse.ws2013.routekit.map;

import java.awt.geom.Line2D;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import edu.kit.pse.ws2013.routekit.util.Coordinates;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;

/**
 * Eine geometrische Datenstruktur zum schnellen Auffinden von Kanten innerhalb
 * eines Kartenausschnitts.
 */
public class GraphIndex {
	private Graph graph;

	private static boolean isSmaller(boolean lat, float threshhold,
			Coordinates from, Coordinates to) {
		return (lat ? from.getLatitude() : from.getLongitude()) <= threshhold
				|| (lat ? to.getLatitude() : to.getLongitude()) <= threshhold;
	}

	private static boolean isGreater(boolean lat, float threshhold,
			Coordinates from, Coordinates to) {
		return (lat ? from.getLatitude() : from.getLongitude()) >= threshhold
				|| (lat ? to.getLatitude() : to.getLongitude()) >= threshhold;
	}

	public class Node {
		Node left;
		Node right;
		float threshhold;
		Set<Integer> contents;
		private boolean splitLat;

		public Node(Set<Integer> contents, boolean splitLat, Graph g) {
			if (contents.size() < 50) {
				this.contents = contents;
			} else {
				this.splitLat = splitLat;
				Set<Integer> low = new HashSet<>();
				Set<Integer> high = new HashSet<>();
				float sum = 0;
				if (splitLat) {
					for (Integer integer : contents) {
						int edge = integer;
						Coordinates a = g.getCoordinates(g.getStartNode(edge));
						Coordinates b = g.getCoordinates(g.getTargetNode(edge));
						float f1 = a.getLatitude();
						float f2 = b.getLatitude();
						sum += f1 + f2;
					}
				} else {
					for (Integer integer : contents) {
						int edge = integer;
						float f1 = g.getCoordinates(g.getStartNode(edge))
								.getLongitude();
						float f2 = g.getCoordinates(g.getTargetNode(edge))
								.getLongitude();
						sum += f1 + f2;
					}
				}
				sum /= 2 * contents.size();
				threshhold = sum;
				for (Integer integer : contents) {
					int edge = integer;
					Coordinates f1 = g.getCoordinates(g.getStartNode(edge));
					Coordinates f2 = g.getCoordinates(g.getTargetNode(edge));
					if (isSmaller(splitLat, sum, f1, f2)) {
						low.add(edge);
					}
					if (isGreater(splitLat, sum, f1, f2)) {
						high.add(edge);
					}
				}
				left = new Node(low, !splitLat, g);
				right = new Node(high, !splitLat, g);
			}
		}

		public void addAll(Graph g, Coordinates leftTop,
				Coordinates rightBottom, Set<Integer> target) {
			if (contents != null) {
				// TODO is more efficient possible
				for (Integer edg : contents) {
					int edge = edg;
					Coordinates from = g.getCoordinates(g.getStartNode(edge));
					Coordinates to = g.getCoordinates(g.getTargetNode(edge));
					float minA = Math.min(from.getLatitude(), to.getLatitude());
					float minO = Math.min(from.getLongitude(),
							to.getLongitude());
					float maxA = Math.max(from.getLatitude(), to.getLatitude());
					float maxO = Math.max(from.getLongitude(),
							to.getLongitude());
					if (minA <= rightBottom.getLatitude()
							&& minO <= rightBottom.getLongitude()
							&& maxA >= leftTop.getLatitude()
							&& maxO >= leftTop.getLongitude()) {
						target.add(edg);
					}
				}
				// target.addAll(contents);
			} else {
				if (isSmaller(splitLat, threshhold, leftTop, rightBottom)) {
					left.addAll(g, leftTop, rightBottom, target);
				}
				if (isGreater(splitLat, threshhold, leftTop, rightBottom)) {
					right.addAll(g, leftTop, rightBottom, target);
				}
			}
		}
	}

	/**
	 * Konstruktor: Erzeugt die Datenstruktur für den gegebenen Graph und die
	 * angegebene Zoomstufe.
	 * 
	 * @param graph
	 *            Ein Graph.
	 * @param maxType
	 *            the maximal {@link HighwayType} to include.
	 */
	public GraphIndex(final Graph graph, final HighwayType maxType) {
		this.graph = graph;
		final int numberOfEdges = graph.getNumberOfEdges();
		final int maxTypeInt = maxType.ordinal();
		root = new Node(new AbstractSet<Integer>() {

			@Override
			public Iterator<Integer> iterator() {
				return new Iterator<Integer>() {
					int current = -1;
					boolean eaten = true;

					private void nextValid() {
						if (eaten) {
							eaten = false;
							while (++current < numberOfEdges) {
								EdgeProperties props = graph.getEdgeProperties(current);
								if (props.getType().ordinal() <= maxTypeInt) {
									return;
								}
							}
						}
					}

					@Override
					public boolean hasNext() {
						nextValid();
						return current < numberOfEdges;
					}

					@Override
					public Integer next() {
						nextValid();
						eaten = true;
						return current;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}

				};
			}

			@Override
			public int size() {
				return numberOfEdges;
			}
		}, false, graph);
	}

	Node root;

	/**
	 * Sucht zu gegebenen Koordinaten den nächsten Punkt auf einer Kante.
	 * 
	 * @param coords
	 *            Die Koordinaten eines Punktes.
	 * @return
	 */
	public PointOnEdge findNearestPointOnEdge(Coordinates coords) {
		Coordinates left = new Coordinates(coords.getLatitude() - 0.1f,
				coords.getLongitude() - 0.1f);
		Coordinates right = new Coordinates(coords.getLatitude() + 0.1f,
				coords.getLongitude() + 0.1f);
		double nearest = Double.POSITIVE_INFINITY;
		int emax = -1;
		for (Integer e : getEdgesInRectangle(left, right)) {
			Coordinates start = graph.getCoordinates(graph.getStartNode(e));
			Coordinates end = graph.getCoordinates(graph.getTargetNode(e));
			double l = Line2D.ptSegDistSq(start.getLatitude(),
					start.getLongitude(), end.getLatitude(),
					end.getLongitude(), coords.getLatitude(),
					coords.getLongitude());
			if (l < nearest) {
				nearest = l;
				emax = e;
			}

		}
		if (emax == -1) {
			return null;
		}
		Coordinates start = graph.getCoordinates(graph.getStartNode(emax));
		Coordinates end = graph.getCoordinates(graph.getTargetNode(emax));
		double path = (coords.getLatitude() - start.getLatitude())
				* (end.getLatitude() - start.getLatitude())
				+ (coords.getLongitude() - start.getLongitude())
				* (end.getLongitude() - start.getLongitude());
		path /= (end.getLatitude() - start.getLatitude())
				* (end.getLatitude() - start.getLatitude())
				+ (end.getLongitude() - start.getLongitude())
				* (end.getLongitude() - start.getLongitude());
		return new PointOnEdge(emax, path < 0 ? 0 : path > 1 ? 1 : (float) path);
	}

	/**
	 * Bestimmt alle Kanten innerhalb eines rechteckigen Kartenausschnitts, der
	 * durch {@code leftTop} und {@code rightBottom} festgelegt ist.
	 * 
	 * @param leftTop
	 *            Die Koordinaten der linken oberen Ecke des Ausschnitts.
	 * @param rightBottom
	 *            Die Koordinaten der rechten unteren Ecke des Ausschnitts.
	 * @return
	 */
	public Set<Integer> getEdgesInRectangle(Coordinates leftTop,
			Coordinates rightBottom) {
		Set<Integer> ints = new TreeSet<>(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				int a = Integer.compare(graph.getEdgeProperties(o1).getType()
						.ordinal(), graph.getEdgeProperties(o2).getType()
						.ordinal());
				if (a != 0) {
					return -a;
				}
				return o1.compareTo(o2);
			}
		});
		root.addAll(graph, leftTop, rightBottom, ints);
		return ints;
	}
}
