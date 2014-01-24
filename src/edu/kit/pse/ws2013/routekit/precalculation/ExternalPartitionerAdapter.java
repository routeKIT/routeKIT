package edu.kit.pse.ws2013.routekit.precalculation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import edu.kit.pse.ws2013.routekit.map.EdgeBasedGraph;

/**
 * Provides the functionality of partitioning a graph by invoking the external
 * program METIS.
 */
public class ExternalPartitionerAdapter implements GraphPartitioner {
	private EdgeBasedGraph graph;

	@Override
	public void partitionGraph(EdgeBasedGraph graph, int numberOfPartitions)
			throws IOException {
		String fileName = System.getProperty("java.io.tmpdir") + "/graph";
		writeGraphFile(fileName);
		// TODO: invoke METIS
		readPartitionFile(fileName + ".part."
				+ String.valueOf(numberOfPartitions));
	}

	private void writeGraphFile(String fileName) throws IOException {
		PrintWriter writer = new PrintWriter(fileName);
		writer.print(graph.getNumberOfEdges());
		writer.print(' ');
		writer.println(graph.getNumberOfTurns());
		for (int edge = 0; edge < graph.getNumberOfEdges(); edge++) {
			StringBuilder line = new StringBuilder();
			for (int turn : graph.getOutgoingTurns(edge)) {
				line.append(graph.getTargetEdge(turn));
				line.append(' ');
			}
			line.deleteCharAt(line.length() - 1);
			writer.println(line);
		}
		writer.close();
	}

	private void readPartitionFile(String fileName) throws IOException {
		int partitions[] = new int[graph.getNumberOfEdges()];

		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		for (int edge = 0; edge < partitions.length; edge++) {
			partitions[edge] = Integer.parseInt(reader.readLine());
		}
		reader.close();

		graph.setPartitions(partitions);
	}
}
