package edu.kit.pse.ws2013.routekit.util;

/**
 * A pair of geographic coordinates.
 */
public class Coordinates {
	private float lat;
	private float lon;

	/**
	 * Creates a new object from the given coordinates.
	 * 
	 * @param lat
	 *            the latitude
	 * @param lon
	 *            the longitude
	 * @throws IllegalArgumentException
	 *             if the coordinates are out of range
	 */
	public Coordinates(float lat, float lon) {
		if (lat < -85 || lat > 85 || lon < -180 || lon > 180) {
			throw new IllegalArgumentException();
		}

		this.lat = lat;
		this.lon = lon;
	}

	/**
	 * Calculates the angle enclosed between a line from this point to the given
	 * first point and a line from this point to the given second point.
	 * 
	 * @param coords1
	 *            the coordinates of the first point
	 * @param coords2
	 *            the coordinates of the second point
	 * @return that angle
	 */
	public float angleBetween(Coordinates coords1, Coordinates coords2) {
		// TODO: auto-generated method stub
		return 0;
	}

	/**
	 * Calculates the distance (air-line, in meters) between these and the given
	 * coordinates.
	 * 
	 * @param other
	 *            the other coordinates
	 * @return the distance between the two coordinates
	 */
	public float distanceTo(Coordinates other) {
		// TODO: auto-generated method stub
		return 0;
	}

	/**
	 * Calculates the coordinates of the point with the specified position on a
	 * line between these coordinates and the given coordinates. This method can
	 * be used to determine the coordinates of a {@link PointOnEdge}.
	 * 
	 * @param to
	 *            indicates the direction into which to go
	 * @param position
	 *            a value between 0 and 1 indicating how far to go into that
	 *            direction
	 * @return the wanted coordinates
	 */
	public Coordinates goIntoDirection(Coordinates to, float position) {
		// TODO: auto-generated method stub
		return to;

	}

	/**
	 * Calculates the SlippyMap tile x-component of these coordinates.
	 * 
	 * @param zoom
	 *            the zoom level
	 * @return the SMT x-component
	 */
	public float getSmtX(int zoom) {
		return (lon + 180) / 360 * (1 << zoom);
	}

	/**
	 * Calculates the SlippyMap tile y-component of these coordinates.
	 * 
	 * @param zoom
	 *            the zoom level
	 * @return the SMT y-component
	 */
	public float getSmtY(int zoom) {
		return (float) ((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1
				/ Math.cos(Math.toRadians(lat)))
				/ Math.PI) / 2 * (1 << zoom));
	}

	/**
	 * Creates a new {@code Coordinates} object from the given SlippyMap tile
	 * coordinates.
	 * 
	 * @param x
	 *            the SMT x-component
	 * @param y
	 *            the SMT y-component
	 * @param zoom
	 *            the zoom level
	 * 
	 * @see <a
	 *      href="http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames">SlippyMap
	 *      tiles in the OSM wiki</a>
	 * @return the coordinates of the given point
	 */
	public static Coordinates fromSmt(float x, float y, int zoom) {
		x %= 1 << zoom;
		y %= 1 << zoom;
		float lon = (float) (x / Math.pow(2.0, zoom) * 360.0 - 180);
		double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, zoom);
		float lat = (float) Math.toDegrees(Math.atan(Math.sinh(n)));
		return new Coordinates(lat, lon);
	}

	/**
	 * Returns the latitude.
	 * 
	 * @return the latitude
	 */
	public float getLatitude() {
		return lat;
	}

	/**
	 * Returns the longitude.
	 * 
	 * @return the longitude
	 */
	public float getLongitude() {
		return lon;
	}

	/**
	 * <p>
	 * Returns a string representation of the coordinates suitable for parsing
	 * with {@link #fromString(String) fromString}.
	 * </p>
	 * <p>
	 * The exact format is: {@code lat lon}, where {@code lat} and {@code lon}
	 * are the latitude and the longitude respectively, in decimal floating
	 * point number format.
	 * </p>
	 * 
	 * @return A string representation of the coordinates.
	 */
	@Override
	public String toString() {
		return lat + " " + lon;
	}

	/**
	 * Parse a coordinates string as returned by {@link #toString()} back into
	 * {@link Coordinates}.
	 * 
	 * @param s
	 *            The coordinates string.
	 * @return The {@link Coordinates} parsed from the string.
	 * @throws IllegalArgumentException
	 *             If the coordinates string can’t be parsed.
	 */
	public static Coordinates fromString(String s) {
		if (s == null) {
			throw new IllegalArgumentException("Coordinates string is null!",
					new NullPointerException());
		}
		String[] coords = s.split(" ");
		if (coords.length != 2) {
			throw new IllegalArgumentException(
					"Coordinates string must contain exactly two space-separated components!");
		}
		float lat;
		float lon;
		try {
			lat = Float.parseFloat(coords[0]);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Can’t parse latitude!", e);
		}
		try {
			lon = Float.parseFloat(coords[1]);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Can’t parse longitude!", e);
		}
		if (lat < -85 || lat > 85
				|| (Math.abs(lat) == 85 && !coords[0].matches("-?85(.0+)?"))) {
			throw new IllegalArgumentException(
					"Latitude must be in range [-85°,85°]!");
		}
		if (lon < -180 || lon > 180
				|| (Math.abs(lon) == 180 && !coords[1].matches("-?180(.0+)?"))) {
			throw new IllegalArgumentException(
					"Longitude must be in range [-180°,180°]!");
		}
		return new Coordinates(lat, lon);
	}
}
