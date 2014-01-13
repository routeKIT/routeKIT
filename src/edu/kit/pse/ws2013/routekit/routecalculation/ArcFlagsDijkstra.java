package edu.kit.pse.ws2013.routekit.routecalculation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

		// List<Integer> turns = new ArrayList<Integer>();

		// Route route = new Route(data, start, destination, turns);

		Route route = calculateDummy(start, destination, data);

		return route;
	}

	private Route calculateDummy(PointOnEdge start, PointOnEdge destination,
			ProfileMapCombination data) {
		List<Integer> turns = new ArrayList<Integer>();
		turns.add(start.getEdge());

		Set<Integer> eins = data.getStreetMap().getEdgeBasedGraph()
				.getOutgoingTurns(start.getEdge());

		for (Integer integer : eins) {
			int turn = data.getStreetMap().getEdgeBasedGraph()
					.getTargetEdge(integer);
			turns.add(turn);
		}

		// turns.add(data.getStreetMap().getEdgeBasedGraph().getTargetEdge(data.getStreetMap().getEdgeBasedGraph().getOutgoingTurns(start.getEdge()).iterator().next()));

		Route route = new Route(data, start, destination, turns);

		return route;
	}
}
