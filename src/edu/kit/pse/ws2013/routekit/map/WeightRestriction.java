package edu.kit.pse.ws2013.routekit.map;

import java.util.HashMap;
import java.util.Map;

import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * A restriction of the vehicle weight.
 */
public class WeightRestriction implements Restriction {
	private static Map<Integer, WeightRestriction> instances = new HashMap<>();
	
	/**
	 * Returns an instance of this class for the specified weight.
	 * 
	 * @param weight
	 *            the maximum allowed weight of the vehicle (in kilograms)
	 * @return the desired instance
	 * @throws IllegalArgumentException if {@code weight} is negative
	 */
	public static Restriction getInstance(int weight) {
		if (weight < 0) {
			throw new IllegalArgumentException();
		}
		
		if (!instances.containsKey(weight)) {
			instances.put(weight, new WeightRestriction(weight));
		}
		return instances.get(weight);
	}
	
	private final int weight;

	private WeightRestriction(int weight) {
		this.weight = weight;
	}

	@Override
	public boolean allows(Profile profile) {
		return profile.getWeight() <= weight;
	}
}
