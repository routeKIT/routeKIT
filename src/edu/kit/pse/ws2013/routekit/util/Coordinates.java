package edu.kit.pse.ws2013.routekit.util;

/**
 * Kapselt ein Paar geographischer Koordinaten.
 */
public class Coordinates {
	float lat;
	float lon;

	/**
	 * Konstruktor: Erstellt ein neues Objekt aus den gegebenen Koordinaten.
	 * 
	 * @param lat
	 *            Der Breitengrad.
	 * @param lon
	 *            Der Längengrad.
	 */
	public Coordinates(float lat, float lon) {
		this.lat = lat;
		this.lon = lon;
	}

	/**
	 * Berechnet den zwischen einer Linie von diesem zum ersten und einer Linie
	 * von diesem zum zweiten Punkt eingeschlossenen Winkel.
	 * 
	 * @param coords1
	 *            Die Koordinaten des ersten Punkts.
	 * @param coords2
	 *            Die Koordinaten des zweiten Punkts.
	 * @return
	 */
	public float angleBetween(Coordinates coords1, Coordinates coords2) {
		return 0;
	}
	/**
	 * Berechnet die Entfernung (Luftlinie, in Metern) zwischen den zwei
	 * Koordinaten.
	 * 
	 * @param other
	 *            Die anderen Koordinaten.
	 * @return
	 */
	public float distanceTo(Coordinates other) {
		return 0;
	}
	/**
	 * Berechnet die SMT-X-Komponente zu diesen Koordinaten.
	 * 
	 * @param zoom
	 *            Die Zoomstufe.
	 * @return
	 */
	public float getSmtX(int zoom) {
		return (lon + 180) / 360 * (1 << zoom);
	}
	/**
	 * Berechnet die SMT-Y-Komponente zu diesen Koordinaten.
	 * 
	 * @param zoom
	 *            Die Zoomstufe.
	 * @return
	 */
	public float getSmtY(int zoom) {
		return (float) ((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1
				/ Math.cos(Math.toRadians(lat)))
				/ Math.PI) / 2 * (1 << zoom));
	}
	/**
	 * Rechnet SlippyMapTile-Koordinaten in Koordinaten um.
	 * 
	 * @param x
	 *            Die SMT-X-Komponente.
	 * @param y
	 *            Die SMT-Y-Komponente.
	 * @param zoom
	 *            Die Zoomstufe.
	 * 
	 * @see <a
	 *      href="http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames">Slippy
	 *      Map Tile im OSM Wiki</a>
	 * @return die Geokoordinaten dieses Punktes
	 */
	public static Coordinates fromSmt(float x, float y, int zoom) {
		x %= 1 << zoom;
		y %= 1 << zoom;
		float lon = (float) (x / Math.pow(2.0, zoom) * 360.0 - 180);
		double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, zoom);
		float lat = (float) Math.toDegrees(Math.atan(Math.sinh(n)));
		return new Coordinates(lat, lon);
	}
	public float getLat() {
		return lat;
	}
	public float getLon() {
		return lon;
	}
}
