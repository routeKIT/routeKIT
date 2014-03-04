package edu.kit.pse.ws2013.routekit.map;

import java.awt.geom.Line2D;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import edu.kit.pse.ws2013.routekit.util.Coordinates;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;

public class ArrayGraphIndex implements GraphIndex {

	private final Graph graph;
	private final GraphView graphView;

	private final int[] contents;

	/**
	 * 3-4 ints per node:
	 * <ol>
	 * <li>0 means splitLon, 1 means splitLat, -1 means direct content</li>
	 * <li>if direct: start (index in contents), else left (index in nodes)</li>
	 * <li>if direct: length (in contents), else right (index in nodes)</li>
	 * <li>if indirect: threshold ({@link Float#floatToIntBits(float)}), else
	 * absent</li>
	 * </ol>
	 */
	private final int[] nodes;

	public ArrayGraphIndex(Graph graph, GraphView graphView, int[] contents,
			int[] nodes) {
		this.graph = graph;
		this.graphView = graphView;
		this.contents = contents;
		this.nodes = nodes;
	}

	@Override
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

	@Override
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
		addAll(0, graph, leftTop, rightBottom, ints);
		return ints;
	}

	/**
	 * Adds all coordinates reachable from the specified node which lie in the
	 * specified rectangle to the given set.
	 */
	private void addAll(int nodeIndex, Graph g, Coordinates leftTop,
			Coordinates rightBottom, Set<Integer> target) {
		switch (nodes[nodeIndex]) {
		case -1: {
			// direct
			for (Integer edg : new IntArraySet(nodes[nodeIndex + 1],
					nodes[nodeIndex + 2], contents)) {
				int edge = edg;
				Coordinates from = g.getCoordinates(graphView
						.getStartNode(edge));
				Coordinates to = g
						.getCoordinates(graphView.getTargetNode(edge));
				float minA = Math.min(from.getLatitude(), to.getLatitude());
				float minO = Math.min(from.getLongitude(), to.getLongitude());
				float maxA = Math.max(from.getLatitude(), to.getLatitude());
				float maxO = Math.max(from.getLongitude(), to.getLongitude());
				if (minA <= rightBottom.getLatitude()
						&& minO <= rightBottom.getLongitude()
						&& maxA >= leftTop.getLatitude()
						&& maxO >= leftTop.getLongitude()) {
					target.add(edg);
				}
			}
			break;
		}
		case 0: {
			// split by lon
			float threshold = Float.intBitsToFloat(nodes[nodeIndex + 3]);
			if (leftTop.getLongitude() <= threshold
					|| rightBottom.getLongitude() <= threshold) {
				addAll(nodes[nodeIndex + 1], g, leftTop, rightBottom, target);
			}
			if (leftTop.getLongitude() >= threshold
					|| rightBottom.getLongitude() >= threshold) {
				addAll(nodes[nodeIndex + 2], g, leftTop, rightBottom, target);
			}
			break;
		}
		case 1: {
			// split by lat
			float threshold = Float.intBitsToFloat(nodes[nodeIndex + 3]);
			if (leftTop.getLatitude() <= threshold
					|| rightBottom.getLatitude() <= threshold) {
				addAll(nodes[nodeIndex + 1], g, leftTop, rightBottom, target);
			}
			if (leftTop.getLatitude() >= threshold
					|| rightBottom.getLatitude() >= threshold) {
				addAll(nodes[nodeIndex + 2], g, leftTop, rightBottom, target);
			}
			break;
		}
		}
	}

	@Override
	public GraphView getView() {
		return graphView;
	}
}
