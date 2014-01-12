package edu.kit.pse.ws2013.routekit.map;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * A restriction of using roads or turns with certain vehicles.
 */
public abstract class Restriction {
	/**
	 * Determines whether the use of a road or turn with this restriction is
	 * allowed under the specified profile.
	 * 
	 * @param profile
	 *            the profile in use
	 * @return {@code true} if this restriction allows the use of the road or
	 *         turn, otherwise {@code false}
	 */
	public abstract boolean allows(Profile profile);

	public void save(DataOutput out) throws IOException {
		if (this instanceof NoRestriction)
			out.writeByte(0);
		else if (this instanceof WidthRestriction)
			out.writeByte(1);
		else if (this instanceof HeightRestriction)
			out.writeByte(2);
		else if (this instanceof WeightRestriction)
			out.writeByte(3);
		else if (this instanceof VehicleTypeRestriction)
			out.writeByte(4);
		else if (this instanceof MultipleRestrictions)
			out.writeByte(5);
		saveInternal(out);
	}

	protected abstract void saveInternal(DataOutput out) throws IOException;

	public static Restriction load(DataInput in) throws IOException {
		switch (in.readByte()) {
		case 0:
			return NoRestriction.loadInternal(in);
		case 1:
			return WidthRestriction.loadInternal(in);
		case 2:
			return HeightRestriction.loadInternal(in);
		case 3:
			return WeightRestriction.loadInternal(in);
		case 4:
			return VehicleTypeRestriction.loadInternal(in);
		case 5:
			return MultipleRestrictions.loadInternal(in);
		default:
			throw new IOException("Unknown restriction identifier!");
		}
	}
}
