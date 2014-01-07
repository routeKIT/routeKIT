package edu.kit.pse.ws2013.routekit.map;

import java.util.EnumMap;
import java.util.Map;

import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.profiles.VehicleType;

/**
 * A restriction of the vehicle type.
 */
public class VehicleTypeRestriction implements Restriction {
	private static Map<VehicleType, VehicleTypeRestriction> instances =
			new EnumMap<>(VehicleType.class);

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
}
