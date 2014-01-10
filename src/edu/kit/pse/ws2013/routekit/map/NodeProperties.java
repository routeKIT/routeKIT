package edu.kit.pse.ws2013.routekit.map;


/**
 * This class encapsulates the properties of a node.
 */
public class NodeProperties {

	private final String junctionRef;
	private final String junctionName;
	private final boolean isMotorwayJunction;
	private final boolean isTrafficLights;

	/**
	 * Creates a new {@link NodeProperties} object with the given fields.
	 * 
	 * @param junctionRef
	 *            The junction reference, see {@link #getJunctionRef()}
	 * @param junctionName
	 *            The junction name, see {@link #getJunctionName()}
	 * @param isMotorwayJunction
	 *            If the junction is a motorway junction, see
	 *            {@link #isMotorwayJunction()}
	 * @param isTrafficLights
	 *            If the junction has traffic lights, see
	 *            {@link #isTrafficLights()}
	 */
	public NodeProperties(String junctionRef, String junctionName,
			boolean isMotorwayJunction, boolean isTrafficLights) {
		this.junctionRef = junctionRef;
		this.junctionName = junctionName;
		this.isMotorwayJunction = isMotorwayJunction;
		this.isTrafficLights = isTrafficLights;
	}

	/**
	 * Gets the reference number of the junction or {@code null} if it’s not a
	 * junction.
	 */
	public String getJunctionRef() {
		return junctionRef;
	}

	/**
	 * Gets the name of the junction or {@code null} if it’s not a junction.
	 */
	public String getJunctionName() {
		return junctionName;
	}

	/**
	 * Determines if the node is a motorway junction.
	 */
	public boolean isMotorwayJunction() {
		return isMotorwayJunction;
	}

	/**
	 * Determines if the node has traffic lights.
	 */
	public boolean isTrafficLights() {
		return isTrafficLights;
	}
}
