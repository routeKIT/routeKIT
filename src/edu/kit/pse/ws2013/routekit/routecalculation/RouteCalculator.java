package edu.kit.pse.ws2013.routekit.routecalculation;

import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;

/**
 * An interface for a route calculation algorithm.
 * 
 * @see ArcFlagsDijkstra
 */
public interface RouteCalculator {
	/**
	 * Calculates a route from the specified start point to the specified
	 * destination point on the given {@link ProfileMapCombination}.
	 * 
	 * @param start
	 *            the start point for the route
	 * @param destination
	 *            the destination point for the route
	 * @param data
	 *            the precalculated map data on which the route to be calculated
	 * @return the calculated route
	 */
	public Route calculateRoute(PointOnEdge start, PointOnEdge destination,
			ProfileMapCombination data);
}
