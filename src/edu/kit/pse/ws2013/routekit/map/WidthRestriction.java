package edu.kit.pse.ws2013.routekit.map;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * A restriction of the vehicle width.
 */
public class WidthRestriction extends Restriction {
	private static Map<Integer, WidthRestriction> instances = new HashMap<>();

	/**
	 * Returns an instance of the class for the specified width.
	 * 
	 * @param width
	 *            the maximum allowed width of a vehicle (in centimeters)
	 * @return the desired instance
	 * @throws IllegalArgumentException
	 *             if {@code width} is negative
	 */
	public static Restriction getInstance(int width) {
		if (width < 0) {
			throw new IllegalArgumentException();
		}

		if (!instances.containsKey(width)) {
			instances.put(width, new WidthRestriction(width));
		}
		return instances.get(width);
	}

	private final int width;

	private WidthRestriction(int width) {
		this.width = width;
	}

	@Override
	public boolean allows(Profile profile) {
		return profile.getWidth() <= width;
	}

	@Override
	protected void saveInternal(DataOutput out) throws IOException {
		out.writeInt(width);
	}

	protected static Restriction loadInternal(DataInput in) throws IOException {
		return getInstance(in.readInt());
	}
}
