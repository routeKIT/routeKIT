package edu.kit.pse.ws2013.routekit.map;

import java.util.Collection;

import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * A combination of multiple restrictions.
 */
public class MultipleRestrictions implements Restriction {
	private static final Restriction nullInstance = new Restriction() {
		@Override
		public boolean allows(Profile p) {
			return true;
		}
	};

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
	public Restriction getInstance(Collection<Restriction> restrictions) {
		if (restrictions == null) {
			throw new IllegalArgumentException();
		}

		if (restrictions.isEmpty()) {
			return nullInstance;
		}
		if (restrictions.size() == 1) {
			return restrictions.iterator().next();
		}
		return new MultipleRestrictions(restrictions);
	}

	private final Restriction[] restrictions;

	private MultipleRestrictions(Collection<Restriction> restrictions) {
		this.restrictions = restrictions.toArray(this.restrictions);
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
}
