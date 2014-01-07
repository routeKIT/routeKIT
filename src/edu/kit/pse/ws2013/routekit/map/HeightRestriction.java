package edu.kit.pse.ws2013.routekit.map;

import java.util.HashMap;
import java.util.Map;

import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * A restriction of the vehicle height.
 */
public class HeightRestriction implements Restriction {
	private static Map<Integer, HeightRestriction> instances = new HashMap<>();

	/**
	 * Returns an instance of this class for the specified height.
	 * 
	 * @param height
	 *            the maximum allowed height of a vehicle (in centimeters)
	 * @return the desired instance
	 * @throws IllegalArgumentException
	 *             if {@code height} is negative
	 */
	public static Restriction getInstance(int height) {
		if (height < 0) {
			throw new IllegalArgumentException();
		}

		if (!instances.containsKey(height)) {
			instances.put(height, new HeightRestriction(height));
		}
		return instances.get(height);
	}

	private final int height;

	private HeightRestriction(int height) {
		this.height = height;
	}

	@Override
	public boolean allows(Profile profile) {
		return profile.getHeight() <= height;
	}
}
