package edu.kit.pse.ws2013.routekit.routecalculation;
import java.util.Iterator;
import java.util.List;

import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;
/**
 * Repräsentiert eine berechnete Route.
 */
public class Route {
	/**
	 * Gibt einen Iterator über die Knoten ({@code Node}) der Route
	 * einschließlich Start- und Zielpunkt zurück. Der Iterator ermittelt diese
	 * dynamisch aus der Liste der Abbiegevorgänge.
	 * 
	 * @return
	 */
	public Iterator<Integer> getNodeIterator() {
		return null;
	}
	/**
	 * Konstruktor: Erzeugt ein neues Routen-Objekt mit den angegebenen
	 * Attributen.
	 * 
	 * @param data
	 *            Die Karte, auf dem die Route berechnet wurde.
	 * @param start
	 *            Der Startpunkt der Route.
	 * @param destination
	 *            Der Zielpunkt der Route.
	 * @param turns
	 *            Die Liste der Abbiegevorgänge der Route.
	 */
	public Route(ProfileMapCombination data, PointOnEdge start,
			PointOnEdge destination, List<Integer> turns) {
	}
	/**
	 * Liefert eine Liste der Abbiegevorgänge, aus denen die Route besteht.
	 * 
	 * @return
	 */
	public List<Integer> getTurns() {
		return null;
	}
}
