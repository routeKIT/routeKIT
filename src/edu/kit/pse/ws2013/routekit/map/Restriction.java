package edu.kit.pse.ws2013.routekit.map;

import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * A restriction of using roads or turns with certain vehicles.
 */
public interface Restriction {
	/**
	 * Determines whether the use of a road or turn with this restriction is
	 * allowed under the specified profile.
	 * 
	 * @param profile
	 *            the profile in use
	 * @return {@code true} if this restriction allows the use of the road or
	 *         turn, otherwise {@code false}
	 */
	public boolean allows(Profile profile);
}
