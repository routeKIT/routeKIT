package edu.kit.pse.ws2013.routekit.map;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * A combination of multiple restrictions.
 */
public class MultipleRestrictions extends Restriction {

	/**
	 * Returns an instance of a {@link Restriction} for the specified
	 * restrictions.
	 * 
	 * @param restrictions
	 *            a set of restrictions
	 * @return the desired instance
	 * @throws IllegalArgumentException
	 *             if {@code restrictions} is {@code null}
	 */
	public static Restriction getInstance(Collection<Restriction> restrictions) {
		if (restrictions == null) {
			throw new IllegalArgumentException();
		}

		if (restrictions.isEmpty()) {
			return NoRestriction.getInstance();
		}
		if (restrictions.size() == 1) {
			return restrictions.iterator().next();
		}
		return new MultipleRestrictions(restrictions);
	}

	private final Restriction[] restrictions;

	private MultipleRestrictions(Collection<Restriction> restrictions) {
		this.restrictions = restrictions.toArray(new Restriction[0]);
	}

	@Override
	public boolean allows(Profile profile) {
		for (Restriction r : restrictions) {
			if (!r.allows(profile)) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void saveInternal(DataOutput out) throws IOException {
		out.writeInt(restrictions.length);
		for (Restriction r : restrictions) {
			r.save(out);
		}
	}

	protected static Restriction loadInternal(DataInput in) throws IOException {
		int length = in.readInt();
		Restriction[] restrictions = new Restriction[length];
		for (int i = 0; i < length; i++) {
			restrictions[i] = Restriction.load(in);
		}
		return getInstance(Arrays.asList(restrictions));
	}
}
