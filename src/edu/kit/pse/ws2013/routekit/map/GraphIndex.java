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
 * A geometrical data structure to quickly find all edges in a certain section
 * of the map.
 */
public class GraphIndex {
	private Graph graph;
	private GraphView view;

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

	private class Node {
		Node left;
		Node right;
		float threshhold;
		Set<Integer> contents;
		private boolean splitLat;

		public Node(Set<Integer> contents, boolean splitLat) {
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
						Coordinates a = graph.getCoordinates(view
								.getStartNode(edge));
						Coordinates b = graph.getCoordinates(view
								.getTargetNode(edge));
						float f1 = a.getLatitude();
						float f2 = b.getLatitude();
						sum += f1 + f2;
					}
				} else {
					for (Integer integer : contents) {
						int edge = integer;
						float f1 = graph
								.getCoordinates(view.getStartNode(edge))
								.getLongitude();
						float f2 = graph.getCoordinates(
								view.getTargetNode(edge)).getLongitude();
						sum += f1 + f2;
					}
				}
				sum /= 2 * contents.size();
				threshhold = sum;
				for (Integer integer : contents) {
					int edge = integer;
					Coordinates f1 = graph.getCoordinates(view
							.getStartNode(edge));
					Coordinates f2 = graph.getCoordinates(view
							.getTargetNode(edge));
					if (isSmaller(splitLat, sum, f1, f2)) {
						low.add(edge);
					}
					if (isGreater(splitLat, sum, f1, f2)) {
						high.add(edge);
					}
				}
				left = new Node(low, !splitLat);
				right = new Node(high, !splitLat);
			}
		}

		public void addAll(Graph g, Coordinates leftTop,
				Coordinates rightBottom, Set<Integer> target) {
			if (contents != null) {
				// TODO is more efficient possible

				for (Integer edg : contents) {
					int edge = edg;
					Coordinates from = g
							.getCoordinates(view.getStartNode(edge));
					Coordinates to = g.getCoordinates(view.getTargetNode(edge));
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
	 * Constructs the data structure for the given {@link Graph} and maximum
	 * {@link HighwayType}.
	 * 
	 * @param graph
	 *            The graph.
	 * @param maxType
	 *            The maximum {@link HighwayType} to include.
	 */
	public GraphIndex(final Graph graph, HighwayType maxType, GraphView view) {
		this.graph = graph;
		this.view = view;
		final int numberOfEdges = view.getNumberOfEdges();
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
		}, false);
	}

	Node root;

	/**
	 * Finds the nearest point on an edge of the map.
	 * 
	 * @param coords
	 *            The coordinates of the point.
	 * @return The nearest point on an edge (edge + part of the edge)
	 */
	public PointOnEdge findNearestPointOnEdge(Coordinates coords) {
		final Coordinates left = new Coordinates(coords.getLatitude() - 0.1f,
				coords.getLongitude() - 0.1f);
		final Coordinates right = new Coordinates(coords.getLatitude() + 0.1f,
				coords.getLongitude() + 0.1f);
		double nearest = Double.POSITIVE_INFINITY;
		int emax = -1;
		final double cx = coords.getSmtX(19);
		final double cy = coords.getSmtY(19);
		for (Integer e : getEdgesInRectangle(left, right)) {
			final Coordinates start = graph.getCoordinates(graph
					.getStartNode(e));
			final Coordinates end = graph
					.getCoordinates(graph.getTargetNode(e));
			final double sx = start.getSmtX(19);
			final double sy = start.getSmtY(19);
			final double ex = end.getSmtX(19);
			final double ey = end.getSmtY(19);
			double l = Line2D.ptSegDistSq(sx, sy, ex, ey, cx, cy);
			if (l < nearest) {
				nearest = l;
				emax = e;
			}

		}
		if (emax == -1) {
			return null;
		}
		final Coordinates start = graph
				.getCoordinates(graph.getStartNode(emax));
		final Coordinates end = graph.getCoordinates(graph.getTargetNode(emax));
		final double sx = start.getSmtX(19);
		final double sy = start.getSmtY(19);
		final double ex = end.getSmtX(19);
		final double ey = end.getSmtY(19);
		double path = (cx - sx) * (ex - sx) + (cy - sy) * (ey - sy);
		path /= (ex - sx) * (ex - sx) + (ey - sy) * (ey - sy);
		return new PointOnEdge(emax, path < 0 ? 0 : path > 1 ? 1 : (float) path);
	}

	/**
	 * Determines all edges within a rectangular section of the graph.
	 * 
	 * @param leftTop
	 *            The coordinates of the upper left corner of the section.
	 * @param rightBottom
	 *            The coordinates of the lower right corner of the section.
	 * @return A {@link Set} of all edges within the given section.
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

	public GraphView getView() {
		return view;
	}
}
