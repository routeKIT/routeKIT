package edu.kit.pse.ws2013.routekit.precalculation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.map.EdgeBasedGraph;
import edu.kit.pse.ws2013.routekit.map.Graph;
import edu.kit.pse.ws2013.routekit.models.ArcFlags;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;

public class ArcFlagsCalculatorImpl implements ArcFlagsCalculator {

	private int[] flagsArray;

	@Override
	public void calculateArcFlags(ProfileMapCombination combination) {
		long t1 = System.currentTimeMillis();
		Graph graph = combination.getStreetMap().getGraph();
		EdgeBasedGraph edgeBasedGraph = combination.getStreetMap()
				.getEdgeBasedGraph();
		flagsArray = new int[edgeBasedGraph.getNumberOfTurns()];
		for (int i = 0; i < flagsArray.length; i++) {
			flagsArray[i] = 0x0;
		}
		List<Set<Integer>> partitions = new ArrayList<Set<Integer>>();
		for (int i = 0; i < graph.getNumberOfEdges(); i++) {
			partitions.get(edgeBasedGraph.getPartition(i)).add(i);
		}
		for (int currentPartition = 0; currentPartition < partitions.size(); currentPartition++) {
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
		}
		long t2 = System.currentTimeMillis();
		int time = (int) (t2 - t1);
		combination.setArcFlags(new ArcFlags(flagsArray), time);
	}

	private void buildReverseShortestPathsTreeAndSetArcFlags(Integer edge) {
		// TODO reverse Dijkstra that doesn't search for a specific edge, but
		// builds the entire shortest paths tree and then sets arc flags
		// accordingly
	}

	private void setFlag(int turn, int partition) {
		flagsArray[turn] |= 0x1 << partition;
	}

}
