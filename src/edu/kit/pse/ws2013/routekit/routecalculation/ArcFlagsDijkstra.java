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

	@Override
	public Route calculateRoute(PointOnEdge start, PointOnEdge destination,
			ProfileMapCombination data) {

		int[] graph = data.getStreetMap().getEdgeBasedGraph().getEdges();
		int[] distance = new int[graph.length];
		int[] previous = new int[graph.length];
		FibonacciHeap fh = new FibonacciHeap();

		int startEdge = start.getEdge();
		int destinationEdge = destination.getEdge();

		List<Integer> turns = new ArrayList<Integer>();
		Map<Integer, FibonacciHeap.Entry> fhList = new HashMap<Integer, FibonacciHeap.Entry>();

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
				break;
			}

			Set<Integer> eins = data.getStreetMap().getEdgeBasedGraph()
					.getOutgoingTurns(u);

			for (Integer integer : eins) {
				int edge = data.getStreetMap().getEdgeBasedGraph()
						.getTargetEdge(integer);

				// Arcflags holen
				int arcFlag = data.getArc().getFlag(edge);

				int arcBit = arcFlag >> destinationPartition & 0x1;

				// arcBit prüfen
				if (arcBit != 0) {
					int alt = distance[u]
							+ data.getWeights().getWeight(integer);

					if (alt < distance[edge]) {
						distance[edge] = alt;
						previous[edge] = u;
						FibonacciHeap.Entry toDecrease = fhList.get(edge);
						fh.decreaseKey(toDecrease, alt);
					}
				}
			}
		}

		// Weg rekonstruieren
		int x = destinationEdge;
		while (previous[x] != -1) {
			turns.add(x);
			x = previous[x];
		}

		Route route = new Route(data, start, destination, turns);

		return route;
	}
}
