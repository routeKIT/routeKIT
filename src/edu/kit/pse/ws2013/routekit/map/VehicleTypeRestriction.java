package edu.kit.pse.ws2013.routekit.map;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.profiles.VehicleType;

/**
 * A restriction of the vehicle type.
 */
public class VehicleTypeRestriction extends Restriction {
	private static Map<VehicleType, VehicleTypeRestriction> instances = new EnumMap<>(
			VehicleType.class);

	/**
	 * Returns an instance of this class with the specified {@link VehicleType}.
	 * 
	 * @param type
	 *            the forbidden vehicle type
	 * @return the desired instance
	 * @throws IllegalArgumentException
	 *             if {@code type} is {@code null}
	 */
	public static Restriction getInstance(VehicleType type) {
		if (type == null) {
			throw new IllegalArgumentException();
		}

		if (!instances.containsKey(type)) {
			instances.put(type, new VehicleTypeRestriction(type));
		}
		return instances.get(type);
	}

	private final VehicleType type;

	private VehicleTypeRestriction(VehicleType type) {
		this.type = type;
	}

	@Override
	public boolean allows(Profile profile) {
		return type != profile.getVehicleType();
	}

	@Override
	protected void saveInternal(DataOutput out) throws IOException {
		out.writeByte(type.ordinal());
	}

	protected static Restriction loadInternal(DataInput in) throws IOException {
		return getInstance(VehicleType.values()[in.readByte()]);
	}
}
