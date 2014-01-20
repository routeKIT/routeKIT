package edu.kit.pse.ws2013.routekit.precalculation;

import java.util.Arrays;

import edu.kit.pse.ws2013.routekit.map.EdgeBasedGraph;

public class DummyGraphPartitioner implements GraphPartitioner {

	@Override
	public void partitionGraph(EdgeBasedGraph graph, int numberOfPartitions) {
		int[] data = new int[graph.getNumberOfEdges()];
		Arrays.fill(data, 1);
		graph.setPartitions(data);
	}

}
