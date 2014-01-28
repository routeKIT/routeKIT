package edu.kit.pse.ws2013.routekit.routecalculation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;

/**
 * Uses Dijkstra’s Algorithm to determine the fastest route between start and
 * destination point for the current {@link ProfileMapCombination}. The
 * calculation is sped up by usage of Arc-Flags.
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

		boolean otherDestination = false;

		LinkedList<Integer> turns = new LinkedList<Integer>();
		Map<Integer, FibonacciHeapEntry> fhList = new HashMap<Integer, FibonacciHeapEntry>();

		// destination edge’s partition
		int destinationPartition = data.getStreetMap().getEdgeBasedGraph()
				.getPartition(destinationEdge);

		// initialization
		distance[startEdge] = 0;
		previous[startEdge] = -1;

		if (data.getStreetMap().getGraph().getCorrespondingEdge(startEdge) != -1) {
			distance[data.getStreetMap().getGraph()
					.getCorrespondingEdge(startEdge)] = 0;
			previous[data.getStreetMap().getGraph()
					.getCorrespondingEdge(startEdge)] = -1;
		}

		for (int i = 0; i < graph.length; i++) {
			if (i != startEdge
					&& i != data.getStreetMap().getGraph()
							.getCorrespondingEdge(startEdge)) {
				distance[i] = Integer.MAX_VALUE;
				previous[i] = -1;
			}

			fhList.put(i, fh.add(i, distance[i]));
		}

		// calculation
		while (!fh.isEmpty()) {
			int u = fh.deleteMin().getValue();
			fhList.remove(u);
			if (u == destinationEdge) {
				// found it!
				break;
			}
			if (u == data.getStreetMap().getGraph()
					.getCorrespondingEdge(destinationEdge)) {
				// found it!
				otherDestination = true;
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

				if (fhList.containsKey(targetEdge)) {
					// fetch Arc-Flags
					int arcFlag = data.getArcFlags().getFlag(currentTurn);
					int arcBit = (arcFlag >> destinationPartition) & 0x1;
					// check arc bit
					if (arcBit != 0) {
						int alt = distance[u]
								+ data.getWeights().getWeight(currentTurn);

						if (alt < distance[targetEdge]) {
							distance[targetEdge] = alt;
							previous[targetEdge] = u;

							FibonacciHeapEntry toDecrease = fhList
									.get(targetEdge);
							fh.decreaseKey(toDecrease, alt);
						}
					}
				}
			}
		}

		// reconstruct way
		int x;
		int newStartEdge = start.getEdge();
		int newDestinationEdge = destination.getEdge();

		if (otherDestination) {
			x = data.getStreetMap().getGraph()
					.getCorrespondingEdge(destinationEdge);
			newDestinationEdge = x;
		} else {
			x = destinationEdge;
		}
		while (previous[x] != -1) {
			Set<Integer> outgoingTurns = data.getStreetMap()
					.getEdgeBasedGraph().getOutgoingTurns(previous[x]);

			for (Integer turn : outgoingTurns) {
				if (data.getStreetMap().getEdgeBasedGraph().getTargetEdge(turn) == x) {
					turns.addFirst(turn);
					break;
				}
			}
			x = previous[x];
		}

		newStartEdge = x;

		PointOnEdge newStart = new PointOnEdge(newStartEdge,
				start.getPosition());
		PointOnEdge newDestination = new PointOnEdge(newDestinationEdge,
				destination.getPosition());

		Route route = new Route(data, newStart, newDestination, turns);

		return route;
	}
}
