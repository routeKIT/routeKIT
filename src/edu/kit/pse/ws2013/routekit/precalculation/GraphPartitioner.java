package edu.kit.pse.ws2013.routekit.precalculation;

import java.io.IOException;

import edu.kit.pse.ws2013.routekit.map.EdgeBasedGraph;

/**
 * An interface for a graph partitioner algorithm.
 */
public interface GraphPartitioner {
	/**
	 * Divides the given edged-based graph into the specified number of
	 * partitions.
	 * 
	 * The partitioning is directly set using the
	 * {@link EdgeBasedGraph#setPartitions} method.
	 * 
	 * @param graph
	 *            the graph to be partitioned
	 * @param numberOfPartitions
	 *            the desired number of partitions
	 * @throws IOException
	 *             if any I/O error occurs
	 */
	public void partitionGraph(EdgeBasedGraph graph, int numberOfPartitions)
			throws IOException;
}
