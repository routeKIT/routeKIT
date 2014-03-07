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

		private final int startPartition;

		public Worker(final int startPartition) {
			this.startPartition = startPartition;
		}

		@Override
		public void run() {
			calculateFlagsOfPartitions(startPartition);
		}

	}

	@Override
	public void calculateArcFlags(final ProfileMapCombination combination,
			final ProgressReporter reporter) {
		final long t1 = System.currentTimeMillis();
		graph = combination.getStreetMap().getGraph();
		edgeBasedGraph = combination.getStreetMap().getEdgeBasedGraph();
		weights = combination.getWeights();
		flagsArray = new int[edgeBasedGraph.getNumberOfTurns()];
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
		edgeBasedGraph.reverseGraph(); // for concurrency issues ensure building
										// now.
		numberOfThreads = Math.max(
				Runtime.getRuntime().availableProcessors() / 2, 1);
		final Worker[] workers = new Worker[numberOfThreads];
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new Worker(i);
			workers[i].start();
		}
		try {
			for (int i = 0; i < workers.length; i++) {
				workers[i].join();
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		reporter.popTask();
		final long t2 = System.currentTimeMillis();
		final int time = (int) (t2 - t1);
		combination.setArcFlags(new ArcFlags(flagsArray), time);
	}

	private void calculateFlagsOfPartitions(final int startPartition) {
		for (int currentPartition = startPartition; currentPartition < partitions
				.size(); currentPartition += numberOfThreads) {
			final int[] flagsArray = new int[this.flagsArray.length];
			final Set<Integer> edgesWithTurnsToOtherPartitions = new HashSet<Integer>();
			for (final Integer edge : partitions.get(currentPartition)) {
				final Set<Integer> incomingTurns = edgeBasedGraph
						.getIncomingTurns(edge);
				for (final Integer turn : incomingTurns) {
					final int startEdgePartition = edgeBasedGraph
							.getPartition(edgeBasedGraph.getStartEdge(turn));
					if (startEdgePartition == currentPartition) {
						if (weights.getWeight(turn) != Integer.MAX_VALUE) {
							setFlag(turn, currentPartition, flagsArray);
						}
					} else {
						edgesWithTurnsToOtherPartitions.add(edge);
					}
				}
			}
			for (final Integer edge : edgesWithTurnsToOtherPartitions) {
				buildReverseShortestPathsTreeAndSetArcFlags(edge, flagsArray);
			}
			synchronized (this.flagsArray) {
				// write back flags
				for (int i = 0; i < flagsArray.length; i++) {
					this.flagsArray[i] |= flagsArray[i];
				}
			}
			synchronized (reporter) {
				reporter.setProgress(currentlyCalculatedPartition
						/ (float) nPartitions);
				currentlyCalculatedPartition++;
			}
		}
	}
}
