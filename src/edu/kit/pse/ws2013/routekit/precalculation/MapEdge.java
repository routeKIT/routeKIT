package edu.kit.pse.ws2013.routekit.precalculation;

/**
 * This class represents an edge in the street map graph. This is only a
 * temporary representation used by the {@link OSMParser}.
 */
public class MapEdge {
	private final int targetNode;
	private final OSMWay way;

	private int id = -1;

	/**
	 * Creates a new object with the given attributes.
	 * 
	 * @param targetNode
	 *            the target node
	 * @param way
	 *            the OSM way to which the edge belongs
	 */
	public MapEdge(int targetNode, OSMWay way) {
		this.targetNode = targetNode;
		this.way = way;
	}

	/**
	 * Returns the target node of this edge.
	 * 
	 * @return the target node
	 */
	public int getTargetNode() {
		return targetNode;
	}

	/**
	 * Returns the OSM way to which this edge belongs.
	 * 
	 * @return the OSM way
	 */
	public OSMWay getWay() {
		return way;
	}

	/**
	 * Returns the identifier of this edge.
	 * 
	 * @return the edge identifier
	 * @throws IllegalStateException
	 *             if the identifier has not been set yet
	 */
	public int getId() {
		if (id < 0) {
			throw new IllegalStateException();
		}

		return id;
	}

	/**
	 * Sets the identifier of this edge.
	 * 
	 * @param id
	 *            the identifier to set
	 */
	public void setId(int id) {
		this.id = id;
	}

}
