package edu.kit.pse.ws2013.routekit.map;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * This class encapsulates the properties of an edge.
 */
public class EdgeProperties {
	private final HighwayType type;
	private final String name;
	private final String roadRef;
	private final int maxSpeed;

	/**
	 * Creates a new object with the given properties.
	 * 
	 * @param type
	 *            the highway type
	 * @param name
	 *            the name of the street, or {@code null} if none
	 * @param roadRef
	 *            the reference number of the road, or {@code null} if none
	 * @param maxSpeed
	 *            the allowed maximum speed (in kilometers per hour), or
	 *            {@code 0} if unspecified
	 * @throws IllegalArgumentException
	 *             if {@code type} is {@code null} or {@code maxSpeed} is
	 *             negative
	 */
	public EdgeProperties(HighwayType type, String name, String roadRef,
			int maxSpeed) {
		if (type == null || (name != null && name.isEmpty())
				|| (roadRef != null && roadRef.isEmpty()) || maxSpeed < 0) {
			throw new IllegalArgumentException();
		}

		this.type = type;
		this.name = name;
		this.roadRef = roadRef;
		this.maxSpeed = maxSpeed;
	}

	/**
	 * Returns the highway type of the edge.
	 * 
	 * @return the highway type
	 */
	public HighwayType getType() {
		return type;
	}

	/**
	 * Returns the street name of the edge.
	 * 
	 * @return the street name, or {@code null} if none
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the road reference number of the edge.
	 * 
	 * @return the road reference number, or {@code null} if none
	 */
	public String getRoadRef() {
		return roadRef;
	}

	/**
	 * Calculates the allowed maximum speed of the edge for the specified
	 * profile.
	 * 
	 * @param profile
	 *            the profile in use
	 * @return the allowed maximum speed (in kilometers per hour)
	 */
	public int getMaxSpeed(Profile profile) {
		int profileSpeed = (type == HighwayType.Motorway || type == HighwayType.Trunk) ? profile
				.getSpeedHighway() : profile.getSpeedRoad();
		if (maxSpeed == 0 || profileSpeed < maxSpeed) {
			return profileSpeed;
		}
		return maxSpeed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + maxSpeed;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((roadRef == null) ? 0 : roadRef.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		EdgeProperties other = (EdgeProperties) obj;
		if (maxSpeed != other.maxSpeed)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (roadRef == null) {
			if (other.roadRef != null)
				return false;
		} else if (!roadRef.equals(other.roadRef))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	/**
	 * Save this {@link EdgeProperties} object to the specified
	 * {@link DataOutput}; it can be loaded again by {@link #load(DataInput)}.
	 * 
	 * <h4>Data format</h4>
	 * 
	 * Pretty straightforward: {@link #getType() type.ordinal()},
	 * {@link #getName() name}, {@link #getRoadRef() roadRef} and
	 * {@link #getMaxSpeed(Profile) maxSpeed} are (in this order) written using
	 * {@link DataOutput#writeUTF(String)} and {@link DataOutput#writeInt(int)}.
	 * 
	 * @param out
	 *            The output.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void save(DataOutput out) throws IOException {
		out.writeInt(type.ordinal());
		out.writeUTF(name == null ? "" : name);
		out.writeUTF(roadRef == null ? "" : roadRef);
		out.writeInt(maxSpeed);
	}

	/**
	 * Load an {@link EdgeProperties} object from the specified
	 * {@link DataInput}. For the data format, see {@link #save(DataOutput)}.
	 * 
	 * @param in
	 *            The input.
	 * @return {@link EdgeProperties} parsed from {@code in}.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public static EdgeProperties load(DataInput in) throws IOException {
		final HighwayType type = HighwayType.values()[in.readInt()];
		final String name = in.readUTF();
		final String roadRef = in.readUTF();
		final int maxSpeed = in.readInt();
		return new EdgeProperties(type, name.isEmpty() ? null : name,
				roadRef.isEmpty() ? null : roadRef, maxSpeed);
	}
}
