package edu.kit.pse.ws2013.routekit.routecalculation;

import java.util.HashMap;
import java.util.LinkedList;
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

	@Override
	public Route calculateRoute(PointOnEdge start, PointOnEdge destination,
			ProfileMapCombination data) {

		int[] graph = data.getStreetMap().getEdgeBasedGraph().getEdges();
		int[] distance = new int[graph.length];
		int[] previous = new int[graph.length];
		FibonacciHeap fh = new FibonacciHeap();

		int startEdge = start.getEdge();
		int destinationEdge = destination.getEdge();

		// System.out.println(startEdge + " und " + destinationEdge);

		LinkedList<Integer> turns = new LinkedList<Integer>();
		Map<Integer, FibonacciHeapEntry> fhList = new HashMap<Integer, FibonacciHeapEntry>();

		// Partition der Zielkante
		int destinationPartition = data.getStreetMap().getEdgeBasedGraph()
				.getPartition(destinationEdge);

		// Initialisierung
		distance[startEdge] = 0;
		previous[startEdge] = -1;

		for (int i = 0; i < graph.length; i++) {
			if (i != startEdge) {
				distance[i] = Integer.MAX_VALUE;
				previous[i] = -1;
			}

			fhList.put(i, fh.add(i, distance[i]));
		}

		// Berechnung
		while (!fh.isEmpty()) {
			int u = fh.deleteMin().getValue();
			if (u == destinationEdge) {
				// GEFUNDEN!
				// System.out.println("Gefunden! " + u);
				break;
			}

			if (distance[u] == Integer.MAX_VALUE) {
				break;
			}

			Set<Integer> outgoingTurns = data.getStreetMap()
					.getEdgeBasedGraph().getOutgoingTurns(u);

			for (Integer currentTurn : outgoingTurns) {
				int targetEdge = data.getStreetMap().getEdgeBasedGraph()
						.getTargetEdge(currentTurn);

				// Arcflags holen
				int arcFlag = data.getArc().getFlag(currentTurn);

				int arcBit = arcFlag >> destinationPartition & 0x1;

				// arcBit prüfen
				if (arcBit != 0) {
					int alt = distance[u]
							+ data.getWeights().getWeight(currentTurn);

					if (alt < distance[targetEdge]) {
						distance[targetEdge] = alt;
						previous[targetEdge] = u;

						FibonacciHeapEntry toDecrease = fhList.get(targetEdge);
						fh.decreaseKey(toDecrease, alt);
					}
				}
			}
		}

		// Weg rekonstruieren
		int x = destinationEdge;
		while (previous[x] != -1) {
			Set<Integer> outgoingTurns = data.getStreetMap()
					.getEdgeBasedGraph().getOutgoingTurns(previous[x]);

			for (Integer turn : outgoingTurns) {
				if (data.getStreetMap().getEdgeBasedGraph().getTargetEdge(turn) == x) {
					turns.addFirst(turn);
					break;
				}
			}
			// System.out.println(x);
			// turns.add(x);
			x = previous[x];
		}
		// System.out.println(x);

		Route route = new Route(data, start, destination, turns);

		return route;
	}
}
