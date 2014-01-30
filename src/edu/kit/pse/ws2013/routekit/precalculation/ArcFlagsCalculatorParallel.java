package edu.kit.pse.ws2013.routekit.precalculation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.models.ArcFlags;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;

/**
 * A working {@link ArcFlagsCalculator} using multiple threads.
 * 
 * @author Fabian Hafner
 * @version 1.0
 * 
 */
public class ArcFlagsCalculatorParallel extends ArcFlagsCalculatorImpl {

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
