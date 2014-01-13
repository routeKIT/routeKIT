package edu.kit.pse.ws2013.routekit.precalculation;

import java.util.Map;

import edu.kit.pse.ws2013.routekit.map.EdgeProperties;
import edu.kit.pse.ws2013.routekit.map.Restriction;

/**
 * A way in an OpenStreetMap file. This is only a helper class used by the
 * {@link OSMParser}.
 */
public class OSMWay {
	/**
	 * Creates a new object from the given OSM tags.
	 * 
	 * @param props
	 *            a list of OSM tags (name/value pairs)
	 */
	public OSMWay(Map<String, String> props) {
	}

	/**
	 * Returns a {@link Restriction} object with the restriction(s) applicable
	 * for this way.
	 * 
	 * @return the restriction(s) of this way, or {@code null} if none
	 */
	public Restriction getRestriction() {
		return null;
	}

	/**
	 * Determines if this way is (part of) a roundabout.
	 * 
	 * @return {@code} if it is a roundabout, otherwise {@code false}
	 */
	public boolean isRoundabout() {
		return false;
	}

	/**
	 * Determines if this way constitutes a one-way street.
	 * 
	 * @return {@code true} if this way is a one-way street, or {@code false} if
	 *         it can be used in both directions
	 */
	public boolean isOneway() {
		return false;
	}

	/**
	 * Returns an {@link EdgeProperties} object containing the properties of
	 * this way.
	 * 
	 * @return the said properties object
	 */
	public EdgeProperties getEdgeProperties() {
		return null;
	}
}
