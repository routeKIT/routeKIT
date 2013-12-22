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
	 * Berechnet die -X-Komponente zu diesen Koordinaten.
	 * 
	 * @param zoom
	 *            Die Zoomstufe.
	 * @return
	 */
	public float getSmtX(int zoom) {
		return 0;
	}
	/**
	 * Berechnet die -Y-Komponente zu diesen Koordinaten.
	 * 
	 * @param zoom
	 *            Die Zoomstufe.
	 * @return
	 */
	public float getSmtY(int zoom) {
		return 0;
	}
	/**
	 * (statisch) Rechnet -Koordinaten in Koordinaten um.
	 * 
	 * @param x
	 *            Die -X-Komponente.
	 * @param y
	 *            Die -Y-Komponente.
	 * @param zoom
	 *            Die Zoomstufe.
	 * @return
	 */
	public static Coordinates fromSmt(float x, float y, int zoom) {
		return null;
	}
}
