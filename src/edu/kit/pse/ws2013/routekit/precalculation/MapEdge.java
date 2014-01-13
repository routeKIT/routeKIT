package edu.kit.pse.ws2013.routekit.precalculation;

/**
 * This class represents an edge in the street map graph. This is only a
 * temporary representation used by the {@link OSMParser}.
 */
public class MapEdge {
	private int targetNode;
	private OSMWay way;

	/**
	 * Creates a new object with the given attributes.
	 * 
	 * @param targetNode
	 *            the OSM node identifier of the target node
	 * @param way
	 *            the OSM way to which the edge belongs
	 */
	public MapEdge(int targetNode, OSMWay way) {
		this.targetNode = targetNode;
		this.way = way;
	}

	/**
	 * Returns the OSM node identifier of the target node.
	 * 
	 * @return the ID of the target node
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
}
