/**
 * 
 */
package edu.kit.pse.ws2013.routekit.routecalculation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.map.EdgeBasedGraph;
import edu.kit.pse.ws2013.routekit.map.Graph;
import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.Weights;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;

/**
 * @author Kevin
 * 
 */
public class Dijkstra implements RouteCalculator {

	int destinationPartition;
	int destinationCorrespondingPartition;

	@Override
	public Route calculateRoute(final PointOnEdge start,
			final PointOnEdge destination, final ProfileMapCombination data) {

		final StreetMap streetMap = data.getStreetMap();
		final Graph graph = streetMap.getGraph();
		final EdgeBasedGraph edgeBasedGraph = streetMap.getEdgeBasedGraph();
		final Weights weights = data.getWeights();
		final int edgeCount = edgeBasedGraph.getNumberOfEdges();
		final int[] distance = new int[edgeCount];
		final int[] previous = new int[edgeCount];
		final FibonacciHeap fh = new FibonacciHeap();

		final int startEdge = start.getEdge();
		final int destinationEdge = destination.getEdge();
		final int startCorrespondingEdge = graph
				.getCorrespondingEdge(startEdge);
		final int destinationCorrespondingEdge = graph
				.getCorrespondingEdge(destinationEdge);

		boolean otherDestination = false;

		final LinkedList<Integer> turns = new LinkedList<>();
		final Map<Integer, FibonacciHeapEntry> fhList = new HashMap<>();
		final Set<Integer> ignore = new HashSet<>();

		// destination edge’s partition
		destinationPartition = edgeBasedGraph.getPartition(destinationEdge);

		if (destinationCorrespondingEdge != -1) {
			destinationCorrespondingPartition = edgeBasedGraph
					.getPartition(destinationCorrespondingEdge);
		} else {
			destinationCorrespondingPartition = -1;
		}

		// initialization
		distance[startEdge] = 1;
		fhList.put(startEdge, fh.add(startEdge, 1));

		if (startCorrespondingEdge != -1) {
			distance[startCorrespondingEdge] = 1;
			fhList.put(startCorrespondingEdge,
					fh.add(startCorrespondingEdge, 1));
		}
		boolean foundRoute = false;

		// calculation
		while (!fh.isEmpty()) {
			final int u = fh.deleteMin().getValue();
			fhList.remove(u);
			ignore.add(u);
			if (u == destinationEdge) {
				// found it!
				foundRoute = true;
				break;
			}
			if (u == destinationCorrespondingEdge) {
				// found it!
				otherDestination = true;
				foundRoute = true;
				break;
			}

			final Set<Integer> outgoingTurns = edgeBasedGraph
					.getOutgoingTurns(u);

			for (final Integer currentTurn : outgoingTurns) {
				final int targetEdge = edgeBasedGraph
						.getTargetEdge(currentTurn);

				if (!ignore.contains(targetEdge)) {

					// check arc bit
					if (allowsTurn(currentTurn)) {
						final int weight = weights.getWeight(currentTurn);
						if (weight == Integer.MAX_VALUE) {
							continue;
						}
						final int alt = distance[u] + weight;

						if (alt < distance[targetEdge]
								|| distance[targetEdge] == 0) {
							distance[targetEdge] = alt;
							previous[targetEdge] = u;

							final FibonacciHeapEntry toDecrease = fhList
									.get(targetEdge);
							if (toDecrease == null) {
								fhList.put(targetEdge, fh.add(targetEdge, alt));
							} else {
								fh.decreaseKey(toDecrease, alt);
							}
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
			x = destinationCorrespondingEdge;
			newDestinationEdge = x;
		} else {
			x = destinationEdge;
		}
		if (!foundRoute) {
			// no route found
			return null;
		} else {
			while (x != startEdge && x != startCorrespondingEdge) {
				final Set<Integer> outgoingTurns = edgeBasedGraph
						.getOutgoingTurns(previous[x]);

				for (final Integer turn : outgoingTurns) {
					if (edgeBasedGraph.getTargetEdge(turn) == x) {
						turns.addFirst(turn);
						break;
					}
				}
				x = previous[x];
			}
		}

		newStartEdge = x;

		final PointOnEdge newStart = new PointOnEdge(newStartEdge,
				(newStartEdge != startEdge) ? (1 - start.getPosition())
						: start.getPosition());
		final PointOnEdge newDestination = new PointOnEdge(newDestinationEdge,
				(newDestinationEdge != destinationEdge) ? (1 - destination
						.getPosition()) : destination.getPosition());

		final Route route = new Route(data, newStart, newDestination, turns);

		return route;
	}

	protected boolean allowsTurn(int turn) {
		return true;
	}
}
