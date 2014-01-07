package edu.kit.pse.ws2013.routekit.map;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
		return (lat ? from.getLat() : from.getLon()) <= threshhold
				|| (lat ? to.getLat() : to.getLon()) <= threshhold;
	}

	private static boolean isGreater(boolean lat, float threshhold,
			Coordinates from, Coordinates to) {
		return (lat ? from.getLat() : from.getLon()) >= threshhold
				|| (lat ? to.getLat() : to.getLon()) >= threshhold;
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
						float f1 = a.getLat();
						float f2 = b.getLat();
						sum += f1 + f2;
					}
				} else {
					for (Integer integer : contents) {
						int edge = integer;
						float f1 = g.getCoordinates(g.getStartNode(edge))
								.getLon();
						float f2 = g.getCoordinates(g.getTargetNode(edge))
								.getLon();
						sum += f1 + f2;
					}
				}
				sum /= 2 * contents.size();
				threshhold = sum;
				for (Integer integer : contents) {
					int edge = integer;
					Coordinates f1 = g.getCoordinates(g.getStartNode(edge));
					Coordinates f2 = g
							.getCoordinates(g.getTargetNode(edge));
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
					float minA = Math.min(from.getLat(), to.getLat());
					float minO = Math.min(from.getLon(), to.getLon());
					float maxA = Math.max(from.getLat(), to.getLat());
					float maxO = Math.max(from.getLon(), to.getLon());
					if(minA<=rightBottom.getLat() && minO<=rightBottom.getLon()
							&&maxA>=leftTop.getLat() && maxO>=leftTop.getLon()){
						target.add(edg);
					}
				}
				//target.addAll(contents);
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
	 * @param zoom
	 *            Die Zoomstufe.
	 */
	public GraphIndex(final Graph graph, int zoom) {
		this.graph = graph;
		root = new Node(new AbstractSet<Integer>() {

			@Override
			public Iterator<Integer> iterator() {
				return new Iterator<Integer>() {
					int current = 0;

					@Override
					public boolean hasNext() {
						return current < graph.getEdgesCount();
					}

					@Override
					public Integer next() {
						return current++;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}

				};
			}

			@Override
			public int size() {
				return graph.getEdgesCount();
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
		return null;
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
		HashSet<Integer> ints = new HashSet<>();
		root.addAll(graph, leftTop, rightBottom, ints);
		return ints;
	}

}
