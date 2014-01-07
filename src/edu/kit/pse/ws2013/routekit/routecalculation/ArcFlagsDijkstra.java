package edu.kit.pse.ws2013.routekit.routecalculation;

import java.util.ArrayList;
import java.util.List;

import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;

/**
 * Verwendet Dijkstra’s Algorithmus, um die schnellste Route zwischen Start- und
 * Zielpunkt für die aktuelle Kombination aus Karte und Profil zu berechnen.
 * Durch Arc-Flags wird die Berechnung beschleunigt.
 */
public class ArcFlagsDijkstra implements RouteCalculator {

	@Override
	public Route calculateRoute(PointOnEdge start, PointOnEdge destination,
			ProfileMapCombination data) {
		
		List<Integer> turns = new ArrayList<Integer>();
		
		Route route = new Route(data, start, destination, turns);
		
		return route;
	}
}
