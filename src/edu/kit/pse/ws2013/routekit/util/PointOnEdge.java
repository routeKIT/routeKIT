package edu.kit.pse.ws2013.routekit.util;

/**
 * A point on an edge.
 */
public class PointOnEdge {
	private int edge;
	private float position;

	/**
	 * Creates a new object with the given attributes.
	 * 
	 * @param edge
	 *            the edge
	 * @param position
	 *            a value between 0 and 1 representing the share of the distance
	 *            between the start of the edge and the point in the total
	 *            length of the edge
	 * @throws IllegalArgumentException
	 *             if {@code edge} is negative or {@code position} is an invalid
	 *             value
	 */
	public PointOnEdge(int edge, float position) {
		if (edge < 0 || position < 0 || position > 1) {
			throw new IllegalArgumentException();
		}

		this.edge = edge;
		this.position = position;
	}

	/**
	 * Returns the edge on which this point lies.
	 * 
	 * @return the edge
	 */
	public int getEdge() {
		return edge;
	}

	/**
	 * Returns the position of this point on the edge.
	 * 
	 * @return a value between 0 and 1 representing the share of the distance
	 *         between the start of the edge and this point in the total length
	 *         of the edge
	 */
	public float getPosition() {
		return position;
	}
}
