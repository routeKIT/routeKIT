/**
 * 
 */
package edu.kit.pse.ws2013.routekit.routecalculation;

import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;

/**
 * @author Kevin
 * 
 */
public class Dijkstra extends ArcFlagsDijkstra implements RouteCalculator {

	@Override
	public Route calculateRoute(PointOnEdge start, PointOnEdge destination,
			ProfileMapCombination data) {
		// TODO Auto-generated method stub
		return super.calculateRoute(start, destination, data);
	}

	@Override
	protected boolean useArcFlags() {
		return false;
	}
}
