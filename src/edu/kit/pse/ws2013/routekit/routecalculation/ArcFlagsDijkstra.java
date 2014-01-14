package edu.kit.pse.ws2013.routekit.routecalculation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;

/**
 * Verwendet Dijkstra’s Algorithmus, um die schnellste Route zwischen Start- und
 * Zielpunkt für die aktuelle Kombination aus Karte und Profil zu berechnen.
 * Durch Arc-Flags wird die Berechnung beschleunigt.
 */
public class ArcFlagsDijkstra implements RouteCalculator {

	int[] graph;
	int[] distance;
	int[] previous;

	FibonacciHeap fh;

	@Override
	public Route calculateRoute(PointOnEdge start, PointOnEdge destination,
			ProfileMapCombination data) {
		// List<Integer> turns = new ArrayList<Integer>();
		// Route route = new Route(data, start, destination, turns);

		Route route = calculateDummy(start, destination, data);

		return route;
	}

	private List<Integer> dijkstra(int start, int destination,
			ProfileMapCombination data) {
		graph = data.getStreetMap().getEdgeBasedGraph().getEdges();
		distance = new int[graph.length];
		previous = new int[graph.length];
		fh = new FibonacciHeap();

		List<Integer> turns = new ArrayList<Integer>();
		Map<Integer, FibonacciHeap.Entry> fhList = new HashMap<Integer, FibonacciHeap.Entry>();

		// Partition der Zielkante
		int destinationPartition = data.getStreetMap().getEdgeBasedGraph()
				.getPartition(destination);

		// Initialisierung
		distance[start] = 0;
		previous[start] = -1;

		for (int i = 0; i < graph.length; i++) {
			if (i != start) {
				distance[i] = Integer.MAX_VALUE;
				previous[i] = -1;
			}

			fhList.put(i, fh.add(i, distance[i]));
		}

		// Berechnung
		while (!fh.isEmpty()) {
			int u = fh.deleteMin().getValue();
			if (u == destination) {
				// GEFUNDEN!
				break;
			}

			Set<Integer> eins = data.getStreetMap().getEdgeBasedGraph()
					.getOutgoingTurns(u);

			for (Integer integer : eins) {
				int edge = data.getStreetMap().getEdgeBasedGraph()
						.getTargetEdge(integer);

				// TODO: Prüfen der Arcflag

				int alt = distance[u] + data.getWeights().getWeight(integer);

				if (alt < distance[edge]) {
					distance[edge] = alt;
					previous[edge] = u;
					FibonacciHeap.Entry toDecrease = fhList.get(edge);
					fh.decreaseKey(toDecrease, alt);
				}
			}
		}

		// Weg rekonstruieren List<Integer> turns = new ArrayList<Integer>();
		int x = destination;
		while (previous[x] != -1) {
			turns.add(x);
			x = previous[x];
		}

		return turns;
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
