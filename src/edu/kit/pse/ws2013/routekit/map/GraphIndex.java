package edu.kit.pse.ws2013.routekit.map;

import java.util.Set;

import edu.kit.pse.ws2013.routekit.util.Coordinates;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;

/**
 * A geometrical data structure to quickly find all edges in a certain section
 * of the map.
 */
public interface GraphIndex {

	/**
	 * Finds the nearest point on an edge of the map.
	 * 
	 * @param coords
	 *            The coordinates of the point.
	 * @return The nearest point on an edge (edge + part of the edge)
	 */
	public PointOnEdge findNearestPointOnEdge(Coordinates coords);

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
			Coordinates rightBottom);

	public GraphView getView();
}
