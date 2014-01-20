package edu.kit.pse.ws2013.routekit.precalculation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.kit.pse.ws2013.routekit.map.EdgeProperties;
import edu.kit.pse.ws2013.routekit.map.HeightRestriction;
import edu.kit.pse.ws2013.routekit.map.HighwayType;
import edu.kit.pse.ws2013.routekit.map.MultipleRestrictions;
import edu.kit.pse.ws2013.routekit.map.Restriction;
import edu.kit.pse.ws2013.routekit.map.WeightRestriction;
import edu.kit.pse.ws2013.routekit.map.WidthRestriction;

/**
 * A way in an OpenStreetMap file. This is only a helper class used by the
 * {@link OSMParser}.
 */
public class OSMWay {
	private Map<String, String> tags;

	/**
	 * Creates a new object from the given OSM tags.
	 * 
	 * @param tags
	 *            a list of OSM tags (name/value pairs)
	 */
	public OSMWay(Map<String, String> tags) {
		this.tags = tags;
	}

	/**
	 * Returns a {@link Restriction} object with the restriction(s) applicable
	 * for this way.
	 * 
	 * @return the restriction(s) of this way, or {@code null} if none
	 */
	public Restriction getRestriction() {
		List<Restriction> restrictions = new ArrayList<>();
		if (tags.containsKey("maxweight")) {
			try {
				int maxWeight = (int) Float.parseFloat(tags.get("maxweight")) * 1000;
				restrictions.add(WeightRestriction.getInstance(maxWeight));
			} catch (NumberFormatException e) {
				// Ignore invalid value for the maxweight tag
			}
		}
		if (tags.containsKey("maxwidth")) {
			try {
				int maxWidth = (int) Float.parseFloat(tags.get("maxweight")) * 100;
				restrictions.add(WidthRestriction.getInstance(maxWidth));
			} catch (NumberFormatException e) {
				// Ignore invalid value for the maxwidth tag
			}
		}
		if (tags.containsKey("maxheight")) {
			try {
				int maxHeight = (int) Float.parseFloat(tags.get("maxheight")) * 100;
				restrictions.add(HeightRestriction.getInstance(maxHeight));
			} catch (NumberFormatException e) {
				// Ignore invalid value for the maxheight tag
			}
		}
		// TODO: vehicle type restrictions missing
		return MultipleRestrictions.getInstance(restrictions);
	}

	/**
	 * Determines whether this way is (part of) a roundabout.
	 * 
	 * @return {@code true} if it is a roundabout, otherwise {@code false}
	 */
	public boolean isRoundabout() {
		return tags.containsKey("junction")
				&& tags.get("junction").equals("roundabout");
	}

	/**
	 * Determines whether this way constitutes a one-way street.
	 * 
	 * @return {@code true} if this way is a one-way street, or {@code false} if
	 *         it can also be used in the opposite direction
	 */
	public boolean isOneway() {
		if (!tags.containsKey("oneway")) {
			return false;
		}
		switch (tags.get("oneway").toLowerCase()) {
		case "yes":
		case "true":
		case "1":
			return true;
		case "no":
		case "false":
		case "0":
		case "-1":
			return false;
		default:
			return isRoundabout() || getHighwayType() == HighwayType.Motorway;
		}
	}

	/**
	 * Determines whether this way constitutes a one-way street in reversed way
	 * order.
	 * 
	 * @return {@code true} if this way is a one-way street in the opposite
	 *         direction, or {@code false} if not
	 */
	public boolean isReversedOneway() {
		return tags.containsKey("oneway") && tags.get("oneway").equals("-1");
	}

	/**
	 * Determines if this way is a (relevant) highway and, if yes, what type it
	 * is.
	 * 
	 * @return the highway type of this way, or {@code null} if no highway
	 */
	public HighwayType getHighwayType() {
		if (!tags.containsKey("highway")) {
			return null;
		}
		switch (tags.get("highway")) {
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
			maxSpeed = Math.max(Integer.parseInt(tags.get("maxspeed")), 0);
		} catch (NumberFormatException e) {
			maxSpeed = 0;
		}

		String name = tags.get("name");
		if (name != null && name.isEmpty()) {
			name = null;
		}
		String ref = tags.get("ref");
		if (ref != null && ref.isEmpty()) {
			ref = null;
		}

		return new EdgeProperties(getHighwayType(), name, ref, maxSpeed);
	}
}
