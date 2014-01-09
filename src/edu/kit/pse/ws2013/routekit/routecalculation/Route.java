package edu.kit.pse.ws2013.routekit.routecalculation;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;

/**
 * A calculated route.
 */
public class Route {
	/**
	 * Gibt einen Iterator über die Knoten ({@code Node}) der Route
	 * einschließlich Start- und Zielpunkt zurück. Der Iterator ermittelt diese
	 * dynamisch aus der Liste der Abbiegevorgänge.
	 * 
	 * @return
	 */

	private ProfileMapCombination data;
	private PointOnEdge start;
	private PointOnEdge destination;
	private List<Integer> turns;

	public Iterator<Integer> getNodeIterator() {
		return turns.iterator();
	}

	/**
	 * Creates a new {@code Route} object with the given attributes.
	 * 
	 * @param data
	 *            the map data on which the route was calculated
	 * @param start
	 *            the start point of the route
	 * @param destination
	 *            the destination point of the route
	 * @param turns
	 *            a list of turns the route consists of
	 */
	public Route(ProfileMapCombination data, PointOnEdge start,
			PointOnEdge destination, List<Integer> turns) {
		this.data = data;
		this.start = start;
		this.destination = destination;
		this.turns = turns;
	}

	/**
	 * Returns the map data on which this route was calculated.
	 * 
	 * @return the map data
	 */
	public ProfileMapCombination getData() {
		return data;
	}

	/**
	 * Returns the start point of this route.
	 * 
	 * @return the start point
	 */
	public PointOnEdge getStart() {
		return start;
	}

	/**
	 * Returns the destination point of this route.
	 * 
	 * @return the destination point
	 */
	public PointOnEdge getDestination() {
		return destination;
	}

	/**
	 * Returns the turns which this route consists of.
	 * 
	 * @return the list of turns
	 */
	public List<Integer> getTurns() {
		return Collections.unmodifiableList(turns);
	}
}
