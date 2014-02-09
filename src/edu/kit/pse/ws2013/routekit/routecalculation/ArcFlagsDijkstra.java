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
 * Uses Dijkstra’s Algorithm to determine the fastest route between start and
 * destination point for the current {@link ProfileMapCombination}. The
 * calculation is sped up by usage of Arc-Flags.
 */
public class ArcFlagsDijkstra implements RouteCalculator {

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
		final int destinationPartition = edgeBasedGraph
				.getPartition(destinationEdge);
		final int destinationCorrespondingPartition;

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

		// calculation
		while (!fh.isEmpty()) {
			final int u = fh.deleteMin().getValue();
			fhList.remove(u);
			ignore.add(u);
			if (u == destinationEdge) {
				// found it!
				break;
			}
			if (u == destinationCorrespondingEdge) {
				// found it!
				otherDestination = true;
				break;
			}

			if (distance[u] == 0) {
				break;
			}

			final Set<Integer> outgoingTurns = edgeBasedGraph
					.getOutgoingTurns(u);

			for (final Integer currentTurn : outgoingTurns) {
				final int targetEdge = edgeBasedGraph
						.getTargetEdge(currentTurn);

				if (!ignore.contains(targetEdge)) {
					// fetch Arc-Flags
					final int arcFlag = data.getArcFlags().getFlag(currentTurn);
					final int arcBit = (arcFlag >> destinationPartition) & 0x1;

					final int correspondingArcBit;
					if (destinationCorrespondingEdge != -1) {
						correspondingArcBit = (arcFlag >> destinationCorrespondingPartition) & 0x1;
					} else {
						correspondingArcBit = 0;
					}

					// check arc bit
					if (arcBit != 0 || correspondingArcBit != 0) {
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
		if (previous[x] == 0 && previous[0] == 0) {
			// no route found
			// TODO return null?
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
}
