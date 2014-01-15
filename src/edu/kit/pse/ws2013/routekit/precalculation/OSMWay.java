package edu.kit.pse.ws2013.routekit.precalculation;

import java.util.Map;

import edu.kit.pse.ws2013.routekit.map.EdgeProperties;
import edu.kit.pse.ws2013.routekit.map.HighwayType;
import edu.kit.pse.ws2013.routekit.map.Restriction;

/**
 * A way in an OpenStreetMap file. This is only a helper class used by the
 * {@link OSMParser}.
 */
public class OSMWay {
	private Map<String, String> props;

	/**
	 * Creates a new object from the given OSM tags.
	 * 
	 * @param props
	 *            a list of OSM tags (name/value pairs)
	 */
	public OSMWay(Map<String, String> props) {
		this.props = props;
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
	 * @return {@code true} if it is a roundabout, otherwise {@code false}
	 */
	public boolean isRoundabout() {
		return props.containsKey("junction")
				&& props.get("junction").equals("roundabout");
	}

	/**
	 * Determines if this way constitutes a one-way street.
	 * 
	 * @return {@code true} if this way is a one-way street, or {@code false} if
	 *         it can be used in both directions
	 */
	public boolean isOneway() {
		if (!props.containsKey("oneway")) {
			return false;
		}
		switch (props.get("oneway").toLowerCase()) {
		case "yes":
		case "true":
		case "1":
			return true;
		case "no":
		case "false":
		case "0":
			return false;
		default:
			return isRoundabout() || getHighwayType() == HighwayType.Motorway;
		}
	}

	/**
	 * Determines if this way is a (relevant) highway and, if yes, what type it
	 * is.
	 * 
	 * @return the highway type of this way, or {@code null} if no highway
	 */
	public HighwayType getHighwayType() {
		switch (props.get("highway")) {
		case "motorway":
		case "motorway_link":
			return HighwayType.Motorway;
		case "trunk":
		case "trunk_link":
			return HighwayType.Trunk;
		case "primary":
		case "primary_link":
			return HighwayType.Primary;
		case "secondary":
		case "secondary_link":
			return HighwayType.Secondary;
		case "tertiary":
		case "tertiary_link":
			return HighwayType.Tertiary;
		case "unclassified":
		case "road":
			return HighwayType.Unclassified;
		case "residential":
			return HighwayType.Residential;
		default:
			return null;
		}
	}

	/**
	 * Returns an {@link EdgeProperties} object containing the properties of
	 * this way.
	 * 
	 * @return the said properties object
	 */
	public EdgeProperties getEdgeProperties() {
		int maxSpeed;
		try {
			maxSpeed = Math.max(Integer.parseInt(props.get("maxspeed")), 0);
		} catch (NumberFormatException e) {
			maxSpeed = 0;
		}

		String name = props.get("name");
		if (name != null && name.isEmpty()) {
			name = null;
		}
		String ref = props.get("ref");
		if (ref != null && ref.isEmpty()) {
			ref = null;
		}

		return new EdgeProperties(getHighwayType(), name, ref, maxSpeed);
	}
}
