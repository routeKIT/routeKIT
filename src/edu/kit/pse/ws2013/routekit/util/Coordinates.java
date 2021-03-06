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
		if (lat < -90 || lat > 90 || lon <= -181 || lon >= 181) {
			throw new IllegalArgumentException();
		}
		if (lat < -85) {
			lat = -85;
		}
		if (lat > 85) {
			lat = 85;
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
		final double b1 = getBearing(this, coords2);
		final double b2 = getBearing(coords1, this);
		return (float) Math.IEEEremainder(b1 - b2, 360) + 180;

	}

	private static double getBearing(Coordinates c1, Coordinates c2) {
		final float lat1 = c1.getLatitude();
		final float lat2 = c2.getLatitude();
		final float lon1 = c1.getLongitude();
		final float lon2 = c2.getLongitude();
		final double dx = Math.cos(Math.PI / 180 * lat1) * (lon2 - lon1);
		final double dy = lat2 - lat1;
		final double angle = 180 / Math.PI * Math.atan2(dy, dx);
		return angle;
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
		double toRad = Math.PI / 180;
		double R = 6371000; // m
		double dLat = (other.lat - lat) * toRad;
		double dLon = (other.lon - lon) * toRad;
		double lat1 = lat * toRad;
		double lat2 = other.lat * toRad;

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
				* Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return (float) (R * c);
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
		return new Coordinates(lat + (to.getLatitude() - lat) * position, lon
				+ (to.getLongitude() - lon) * position);

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
		int limit = 1 << zoom;
		x %= limit;
		y %= limit;
		if (x < 0) {
			x = (x + limit) % limit;
		}
		if (y < 0) {
			y = (y + limit) % limit;
		}
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

	@Override
	public boolean equals(Object other) {
		if (other instanceof Coordinates) {
			return lat == ((Coordinates) other).lat
					&& lon == ((Coordinates) other).lon;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(lat);
		result = prime * result + Float.floatToIntBits(lon);
		return result;
	}

	/**
	 * Parses a coordinates string as returned by {@link #toString()} back into
	 * {@link Coordinates}.
	 * 
	 * @param s
	 *            the coordinates string
	 * @return The {@link Coordinates} parsed from the string.
	 * @throws IllegalArgumentException
	 *             if the coordinates string cannot be parsed
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

		float lat = parseLatitude(coords[0]);
		float lon = parseLongitude(coords[1]);

		return new Coordinates(lat, lon);
	}

	/**
	 * Parses a latitude specification from a string.
	 * 
	 * @param s
	 *            the string to be parsed
	 * @return the latitude parsed from the string
	 * @throws IllegalArgumentException
	 *             if the string cannot be parsed
	 */
	public static float parseLatitude(String s) {
		if (s == null) {
			throw new IllegalArgumentException();
		}

		float lat;
		try {
			lat = Float.parseFloat(s);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Can’t parse latitude!", e);
		}
		if (lat < -85 || lat > 85
				|| (Math.abs(lat) == 85 && !s.matches("-?85(.0+)?"))) {
			throw new IllegalArgumentException(
					"Latitude must be in range [-85°,85°]!");
		}
		return lat;
	}

	/**
	 * Parses a longitude specification from a string.
	 * 
	 * @param s
	 *            the string to be parsed
	 * @return the longitude parsed from the string
	 * @throws IllegalArgumentException
	 *             if the string cannot be parsed
	 */
	public static float parseLongitude(String s) {
		if (s == null) {
			throw new IllegalArgumentException();
		}

		float lon;
		try {
			lon = Float.parseFloat(s);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Can’t parse longitude!", e);
		}
		if (lon < -180 || lon > 180
				|| (Math.abs(lon) == 180 && !s.matches("-?180(.0+)?"))) {
			throw new IllegalArgumentException(
					"Longitude must be in range [-180°,180°]!");
		}
		return lon;
	}
}
