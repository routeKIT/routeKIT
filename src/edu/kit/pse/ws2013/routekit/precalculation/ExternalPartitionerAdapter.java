package edu.kit.pse.ws2013.routekit.precalculation;

import edu.kit.pse.ws2013.routekit.map.EdgeBasedGraph;

/**
 * Provides the functionality of partitioning a graph by invoking the external
 * program METIS.
 */
public class ExternalPartitionerAdapter implements GraphPartitioner {
	@Override
	public void partitionGraph(EdgeBasedGraph graph, int numberOfPartitions) {
	}
}
