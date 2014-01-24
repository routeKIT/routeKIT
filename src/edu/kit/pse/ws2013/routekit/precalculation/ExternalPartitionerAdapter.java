package edu.kit.pse.ws2013.routekit.precalculation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
		StringBuilder output = new StringBuilder();
		output.append(graph.getNumberOfEdges()).append(' ');
		int turnNumberPosition = output.length();
		output.append(System.lineSeparator());

		int numberOfTurns = 0;
		for (int edge = 0; edge < graph.getNumberOfEdges(); edge++) {
			// Convert directed to undirected turns (graph edges)
			Set<Integer> edges = new HashSet<>();
			for (int turn : graph.getOutgoingTurns(edge)) {
				edges.add(graph.getTargetEdge(turn));
			}
			for (int turn : graph.getIncomingTurns(edge)) {
				edges.add(graph.getStartEdge(turn));
			}
			numberOfTurns += edges.size();

			for (int targetEdge : edges) {
				output.append(targetEdge).append(' ');
			}
			output.deleteCharAt(output.length() - 1); // remove trailing space
			output.append(System.lineSeparator());
		}

		output.insert(turnNumberPosition, numberOfTurns);

		FileWriter writer = new FileWriter(fileName);
		writer.append(output);
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
