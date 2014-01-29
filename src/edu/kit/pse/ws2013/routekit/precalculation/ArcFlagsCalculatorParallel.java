package edu.kit.pse.ws2013.routekit.precalculation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
 * A working {@link ArcFlagsCalculator} using multiple threads.
 * 
 * @author Fabian Hafner
 * @version 1.0
 * 
 */
public class ArcFlagsCalculatorParallel implements ArcFlagsCalculator {

	private int[] flagsArray;
	private Graph graph;
	private EdgeBasedGraph edgeBasedGraph;
	private Weights weights;
	private final int nPartitions = 32;
	private int currentlyCalculatedPartition;
	private ProgressReporter reporter;
	private List<Set<Integer>> partitions;
	private int numberOfThreads;

	private class Worker extends Thread {

		private int startPartition;

		public Worker(int startPartition) {
			this.startPartition = startPartition;
		}

		@Override
		public void run() {
			calculateFlagsOfPartitions(startPartition);
		}

	}

	@Override
	public void calculateArcFlags(ProfileMapCombination combination,
			ProgressReporter reporter) {
		long t1 = System.currentTimeMillis();
		graph = combination.getStreetMap().getGraph();
		edgeBasedGraph = combination.getStreetMap().getEdgeBasedGraph();
		weights = combination.getWeights();
		flagsArray = new int[edgeBasedGraph.getNumberOfTurns()];
		for (int i = 0; i < flagsArray.length; i++) {
			flagsArray[i] = 0x0;
		}
		this.reporter = reporter;
		partitions = new ArrayList<Set<Integer>>(nPartitions);
		reporter.setSubTasks(new float[] { .1f, .9f });
		reporter.pushTask("Bereite Partitionen vor");
		for (int i = 0; i < nPartitions; i++) {
			partitions.add(new HashSet<Integer>());
		}
		for (int i = 0; i < graph.getNumberOfEdges(); i++) {
			partitions.get(edgeBasedGraph.getPartition(i)).add(i);
		}
		reporter.nextTask("Baue Kürzeste-Pfade-Bäume zu den Schnittkanten der Partitionen auf");
		currentlyCalculatedPartition = 1;
		edgeBasedGraph.getIncomingTurns(1);
		numberOfThreads = Math.max(
				Runtime.getRuntime().availableProcessors() / 2, 1);
		Worker[] workers = new Worker[numberOfThreads];
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new Worker(i);
			workers[i].start();
		}
		try {
			for (int i = 0; i < workers.length; i++) {
				workers[i].join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		reporter.popTask();
		long t2 = System.currentTimeMillis();
		int time = (int) (t2 - t1);
		combination.setArcFlags(new ArcFlags(flagsArray), time);
	}

	private void calculateFlagsOfPartitions(int startPartition) {
		for (int currentPartition = startPartition; currentPartition < partitions
				.size(); currentPartition += numberOfThreads) {
			Set<Integer> edgesWithTurnsToOtherPartitions = new HashSet<Integer>();
			for (Integer edge : partitions.get(currentPartition)) {
				Set<Integer> incomingTurns = edgeBasedGraph
						.getIncomingTurns(edge);
				for (Integer turn : incomingTurns) {
					int startEdgePartition = edgeBasedGraph
							.getPartition(edgeBasedGraph.getStartEdge(turn));
					if (startEdgePartition == currentPartition) {
						setFlag(turn, currentPartition);
					} else {
						edgesWithTurnsToOtherPartitions.add(edge);
					}
				}
			}
			for (Integer edge : edgesWithTurnsToOtherPartitions) {
				buildReverseShortestPathsTreeAndSetArcFlags(edge);
			}
			synchronized (reporter) {
				reporter.setProgress(currentlyCalculatedPartition
						/ (float) nPartitions);
				currentlyCalculatedPartition++;
			}
		}
	}

	/**
	 * Builds the reverse shortest paths tree starting from the given edge using
	 * a reverse Dijkstra and then sets arc flags accordingly.
	 * 
	 * @param edge
	 *            the start edge of the shortest paths tree
	 */
	private void buildReverseShortestPathsTreeAndSetArcFlags(Integer edge) {
		int[] edges = edgeBasedGraph.getEdges();
		int[] distance = new int[edges.length];
		int[] next = new int[edges.length];
		FibonacciHeap fh = new FibonacciHeap();
		Map<Integer, FibonacciHeapEntry> fhList = new HashMap<Integer, FibonacciHeapEntry>();
		int edgePartition = edgeBasedGraph.getPartition(edge);
		distance[edge] = 0;
		next[edge] = -1;
		for (int i = 0; i < edges.length; i++) {
			if (i != edge) {
				distance[i] = Integer.MAX_VALUE;
				next[i] = -1;
			}
			fhList.put(i, fh.add(i, distance[i]));
		}
		while (!fh.isEmpty()) {
			int currentEdge = fh.deleteMin().getValue();
			fhList.remove(currentEdge);
			if (distance[currentEdge] == Integer.MAX_VALUE) {
				break;
			}
			Set<Integer> incomingTurns = edgeBasedGraph
					.getIncomingTurns(currentEdge);
			for (Integer currentTurn : incomingTurns) {
				int startEdge = edgeBasedGraph.getStartEdge(currentTurn);
				int startPartition = edgeBasedGraph.getPartition(startEdge);
				int endEdge = edgeBasedGraph.getTargetEdge(currentTurn);
				int endPartition = edgeBasedGraph.getPartition(endEdge);

				if (fhList.containsKey(startEdge)
						&& !(startPartition == edgePartition && startPartition == endPartition)) {
					int newDistance = distance[currentEdge]
							+ weights.getWeight(currentTurn);

					if (newDistance < distance[startEdge]) {
						distance[startEdge] = newDistance;
						next[startEdge] = currentEdge;

						FibonacciHeapEntry toDecrease = fhList.get(startEdge);
						fh.decreaseKey(toDecrease, newDistance);
					}
				}
			}
		}
		for (int currentEdge = 0; currentEdge < next.length; currentEdge++) {
			int nextEdge = next[currentEdge];
			if (nextEdge != -1) {
				for (Integer turn : edgeBasedGraph
						.getOutgoingTurns(currentEdge)) {
					if (edgeBasedGraph.getTargetEdge(turn) == nextEdge) {
						setFlag(turn, edgePartition);
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
	synchronized private void setFlag(int turn, int partition) {
		if (partition < 0 || partition >= 32) {
			throw new IllegalArgumentException(partition
					+ " isn't a valid partition. Valid partitions: 0 - 31");
		}
		flagsArray[turn] |= 0x1 << partition;
	}

}
