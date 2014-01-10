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
		if (type == null || maxSpeed < 0) {
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

	public void save(DataOutput out) throws IOException {
		out.writeInt(type.ordinal());
		out.writeUTF(name);
		out.writeUTF(roadRef);
		out.writeInt(maxSpeed);
	}

	public static EdgeProperties load(DataInput in) throws IOException {
		return new EdgeProperties(HighwayType.values()[in.readInt()], // HighwayType
				in.readUTF(), // name
				in.readUTF(), // roadRef
				in.readInt());// maxSpeed
	}
}
