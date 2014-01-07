package edu.kit.pse.ws2013.routekit.routecalculation;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

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

	ProfileMapCombination data = null;
	PointOnEdge start = null;
	PointOnEdge destination = null;
	List<Integer> turns = new ArrayList<Integer>();

	public Iterator<Integer> getNodeIterator() {
		return turns.iterator();
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
		this.data = data;
		this.start = start;
		this.destination = destination;
		this.turns = turns;
	}

	/**
	 * Liefert eine Liste der Abbiegevorgänge, aus denen die Route besteht.
	 * 
	 * @return
	 */
	public List<Integer> getTurns() {
		return turns;
	}
}
