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
		if ((junctionRef != null && junctionRef.isEmpty())
				|| (junctionName != null && junctionName.isEmpty())) {
			throw new IllegalArgumentException();
		}

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

	/**
	 * Save this {@link NodeProperties} object to the specified
	 * {@link DataOutput}; it can be loaded again by {@link #load(DataInput)}.
	 * 
	 * <h4>Data format</h4>
	 * 
	 * Pretty straightforward: {@link #getJunctionRef() junctionRef},
	 * {@link #getJunctionName() junctionName}, {@link #isMotorwayJunction()
	 * isMotorwayJunction} and {@link #isTrafficLights() isTrafficLights} are
	 * (in this order) written using {@link DataOutput#writeUTF(String)} and
	 * {@link DataOutput#writeBoolean(boolean)}.
	 * 
	 * @param out
	 *            The output.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void save(DataOutput out) throws IOException {
		out.writeUTF(junctionRef == null ? "" : junctionRef);
		out.writeUTF(junctionName == null ? "" : junctionName);
		out.writeBoolean(isMotorwayJunction);
		out.writeBoolean(isTrafficLights);
	}

	/**
	 * Loads a {@link NodeProperties} object from the specified
	 * {@link DataInput}. For the data format, see {@link #save(DataOutput)}.
	 * 
	 * @param in
	 *            The input.
	 * @return {@link NodeProperties} parsed from {@code in}.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public static NodeProperties load(DataInput in) throws IOException {
		final String junctionRef = in.readUTF();
		final String junctionName = in.readUTF();
		final boolean isMotorwayJunction = in.readBoolean();
		final boolean isTrafficLights = in.readBoolean();
		return new NodeProperties(junctionRef.isEmpty() ? null : junctionRef,
				junctionName.isEmpty() ? null : junctionName,
				isMotorwayJunction, isTrafficLights);
	}
}
