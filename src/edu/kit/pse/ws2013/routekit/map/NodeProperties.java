package edu.kit.pse.ws2013.routekit.map;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isMotorwayJunction ? 1231 : 1237);
		result = prime * result + (isTrafficLights ? 1231 : 1237);
		result = prime * result
				+ ((junctionName == null) ? 0 : junctionName.hashCode());
		result = prime * result
				+ ((junctionRef == null) ? 0 : junctionRef.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeProperties other = (NodeProperties) obj;
		if (isMotorwayJunction != other.isMotorwayJunction)
			return false;
		if (isTrafficLights != other.isTrafficLights)
			return false;
		if (junctionName == null) {
			if (other.junctionName != null)
				return false;
		} else if (!junctionName.equals(other.junctionName))
			return false;
		if (junctionRef == null) {
			if (other.junctionRef != null)
				return false;
		} else if (!junctionRef.equals(other.junctionRef))
			return false;
		return true;
	}

	public void save(DataOutput out) throws IOException {
		out.writeUTF(junctionRef);
		out.writeUTF(junctionName);
		out.writeBoolean(isMotorwayJunction);
		out.writeBoolean(isTrafficLights);
	}

	public static NodeProperties load(DataInput in) throws IOException {
		return new NodeProperties(in.readUTF(), // junctionRef
				in.readUTF(), // junctionName
				in.readBoolean(), // isMotorwayJunction
				in.readBoolean()); // isTrafficLights
	}
}
