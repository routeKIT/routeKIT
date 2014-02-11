package edu.kit.pse.ws2013.routekit.precalculation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.map.EdgeBasedGraph;
import edu.kit.pse.ws2013.routekit.map.Graph;
import edu.kit.pse.ws2013.routekit.models.ArcFlags;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;
import edu.kit.pse.ws2013.routekit.models.Weights;
import edu.kit.pse.ws2013.routekit.routecalculation.FibonacciHeap;
import edu.kit.pse.ws2013.routekit.routecalculation.FibonacciHeapEntry;

/**
 * A working {@link ArcFlagsCalculator}.
 * 
 * @author Fabian Hafner
 * @version 1.0
 * 
 */
public class ArcFlagsCalculatorImpl implements ArcFlagsCalculator {

	protected int[] flagsArray;
	protected Graph graph;
	protected EdgeBasedGraph edgeBasedGraph;
	protected Weights weights;
	protected final int nPartitions = 32;

	@Override
	public void calculateArcFlags(final ProfileMapCombination combination,
			final ProgressReporter reporter) {
		final long t1 = System.currentTimeMillis();
		initData(combination);
		reporter.setSubTasks(new float[] { .1f, .9f });
		reporter.pushTask("Bereite Partitionen vor");
		final List<Set<Integer>> partitions = new ArrayList<Set<Integer>>(
				nPartitions);
		for (int i = 0; i < nPartitions; i++) {
			partitions.add(new HashSet<Integer>());
		}
		for (int i = 0; i < graph.getNumberOfEdges(); i++) {
			partitions.get(edgeBasedGraph.getPartition(i)).add(i);
		}
		reporter.nextTask("Baue Kürzeste-Pfade-Bäume zu den Schnittkanten der Partitionen auf");
		int i = 1;
		for (int currentPartition = 0; currentPartition < partitions.size(); currentPartition++) {
			final Set<Integer> edgesWithTurnsToOtherPartitions = new HashSet<Integer>();
			for (final Integer edge : partitions.get(currentPartition)) {
				final Set<Integer> incomingTurns = edgeBasedGraph
						.getIncomingTurns(edge);
				for (final Integer turn : incomingTurns) {
					final int startEdgePartition = edgeBasedGraph
							.getPartition(edgeBasedGraph.getStartEdge(turn));
					if (startEdgePartition == currentPartition) {
						setFlag(turn, currentPartition, flagsArray);
					} else {
						edgesWithTurnsToOtherPartitions.add(edge);
					}
				}
			}
			for (final Integer edge : edgesWithTurnsToOtherPartitions) {
				buildReverseShortestPathsTreeAndSetArcFlags(edge, flagsArray);
			}
			reporter.setProgress(i / (float) nPartitions);
			i++;
		}
		reporter.popTask();
		final long t2 = System.currentTimeMillis();
		final int time = (int) (t2 - t1);
		combination.setArcFlags(new ArcFlags(flagsArray), time);
	}

	protected void initData(final ProfileMapCombination combination) {
		graph = combination.getStreetMap().getGraph();
		edgeBasedGraph = combination.getStreetMap().getEdgeBasedGraph();
		weights = combination.getWeights();
		flagsArray = new int[edgeBasedGraph.getNumberOfTurns()];
	}

	/**
	 * Builds the reverse shortest paths tree starting from the given edge using
	 * a reverse Dijkstra and then sets arc flags accordingly.
	 * 
	 * @param edge
	 *            the start edge of the shortest paths tree
	 */
	protected void buildReverseShortestPathsTreeAndSetArcFlags(
			final Integer edge, final int[] flagsArray) {
		final int edgeCount = edgeBasedGraph.getNumberOfEdges();
		final int[] distance = new int[edgeCount];
		final int[] next = new int[edgeCount];
		final FibonacciHeap fh = new FibonacciHeap();
		final FibonacciHeapEntry[] fhList = new FibonacciHeapEntry[edgeCount];
		final int edgePartition = edgeBasedGraph.getPartition(edge);

		Arrays.fill(distance, Integer.MAX_VALUE);
		Arrays.fill(next, -1);

		distance[edge] = 0;
		fhList[edge] = fh.add(edge, 0);

		while (!fh.isEmpty()) {
			final int currentEdge = fh.deleteMin().getValue();
			fhList[currentEdge] = null;
			if (distance[currentEdge] == Integer.MAX_VALUE) {
				break;
			}
			final Set<Integer> incomingTurns = edgeBasedGraph
					.getIncomingTurns(currentEdge);
			for (final Integer currentTurn : incomingTurns) {
				final int startEdge = edgeBasedGraph.getStartEdge(currentTurn);
				final int startPartition = edgeBasedGraph
						.getPartition(startEdge);
				final int endEdge = edgeBasedGraph.getTargetEdge(currentTurn);
				final int endPartition = edgeBasedGraph.getPartition(endEdge);

				if (!(startPartition == edgePartition && startPartition == endPartition)) {
					final int weight = weights.getWeight(currentTurn);
					if (weight == Integer.MAX_VALUE) {
						continue;
					}
					final int newDistance = distance[currentEdge] + weight;

					if (newDistance < distance[startEdge]) {
						distance[startEdge] = newDistance;
						next[startEdge] = currentEdge;

						final FibonacciHeapEntry toDecrease = fhList[startEdge];

						if (toDecrease == null) {
							fhList[startEdge] = fh.add(startEdge, newDistance);
						} else {
							fh.decreaseKey(toDecrease, newDistance);
						}
					}
				}
			}
		}
		for (int currentEdge = 0; currentEdge < next.length; currentEdge++) {
			final int nextEdge = next[currentEdge];
			if (nextEdge != -1) {
				for (final Integer turn : edgeBasedGraph
						.getOutgoingTurns(currentEdge)) {
					if (edgeBasedGraph.getTargetEdge(turn) == nextEdge) {
						setFlag(turn, edgePartition, flagsArray);
						break;
					}
				}
			}
		}
	}

	/**
	 * Sets the given turn's arc flag for the given partition to 1.
	 * 
	 * @param turn
	 *            the turn
	 * @param partition
	 *            the partition
	 */
	protected void setFlag(final int turn, final int partition,
			final int[] flagsArray) {
		flagsArray[turn] |= 0x1 << partition;
	}

}
