package edu.kit.pse.ws2013.routekit.precalculation;

import edu.kit.pse.ws2013.routekit.map.EdgeBasedGraph;

/**
 * Leitet die Partitionierungsanfrage an ein externes Partitionierungsprogramm
 * weiter.
 */
public class ExternalPartitionerAdapter {
	/**
	 * Lässt den Graphen durch das externe Programm in die gewünschte Anzahl an
	 * Partitionen teilen.
	 * 
	 * @param graph
	 *            Der zu partitionierende Graph.
	 * @param numberOfPartitions
	 *            Die gewünschte Anzahl an Partitionen.
	 */
	public void partitionGraph(EdgeBasedGraph graph, int numberOfPartitions) {
	}
}
