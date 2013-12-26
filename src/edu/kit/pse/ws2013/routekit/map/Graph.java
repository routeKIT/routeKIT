package edu.kit.pse.ws2013.routekit.map;
import java.util.Map;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.util.Coordinates;
/**
 * Ein Kartengraph/Straßennetz. Beachte: Dieser Graph ist nicht das Ergebnis
 * einer Vorberechnung für ein Profil und eine Karte, sondern nur für eine
 * Karte.
 */
public class Graph {
	int[] nodes;
	int[] edges;
	int[] edgesReverse;
	Map<Integer, NodeProperties> nodeProps;
	EdgeProperties[] edgeProps;
	float[] lat;
	float[] lon;
	/**
	 * Konstruktor: Erzeugt ein neues Graph-Objekt aus dem gegebenen
	 * Adjazenzfeld.
	 * 
	 * @param nodes
	 *            Der Knoten-Bestandteil des Adjazenzfeldes.
	 * @param edges
	 *            Der Kanten-Bestandteil des Adjazenzfeldes.
	 * @param nodeProps
	 *            Die {@code NodeProperties} der Knoten des Graphen. Es wird
	 *            eine {@code Map} anstelle eines Arrays verwendet, da die
	 *            meisten Knoten keine besonderen Eigenschaften haben und daher
	 *            das Array zum großen Teil leer wäre.
	 * 
	 * @param edgeProps
	 *            Die {@code EdgeProperties} der Kanten des Graphen. Hier wird
	 *            ein Array verwendet, da jede Kante einen Namen und damit ein
	 *            {@code EdgeProperties}-Objekt hat.
	 * 
	 * @param lat
	 *            Die geographischen Breiten der Knoten des Graphen.
	 * @param lon
	 *            Die geographischen Längen der Knoten des Graphen.
	 */
	public Graph(int[] nodes, int[] edges,
			Map<Integer, NodeProperties> nodeProps, EdgeProperties[] edgeProps,
			float[] lat, float[] lon) {
		this.nodes = nodes;
		this.edges = edges;
		this.nodeProps = nodeProps;
		this.edgeProps = edgeProps;
		this.lat = lat;
		this.lon = lon;
		this.edgesReverse = new int[edges.length];
		int currentNode = 0;
		for (int i = 0; i < edges.length; i++) {
			while (currentNode + 1 < nodes.length
					&& nodes[currentNode + 1] <= i) {
				currentNode++;

			}
			edgesReverse[i] = currentNode;
		}
	}

	/**
	 * Gibt die {@code NodeProperties} des angegebenen Knotens zurück.
	 * 
	 * @param node
	 *            Der Knoten, dessen {@link NodeProperties} gesucht werden.
	 * @return
	 */
	public NodeProperties getNodeProperties(int node) {
		return nodeProps.get(node);
	}
	/**
	 * Gibt die Koordinaten des angegebenen Knotens zurück.
	 * 
	 * @param node
	 *            Der Knoten, dessen Koordinaten gesucht werden.
	 * @return
	 */
	public Coordinates getCoordinates(int node) {
		return new Coordinates(lat[node], lon[node]);
	}
	/**
	 * Gibt den Startknoten der angegebenen Kante zurück.
	 * 
	 * @param edge
	 *            Die Kante, dessen Startknoten gesucht wird.
	 * @return
	 */
	public int getStartNode(int edge) {
		return edgesReverse[edge];
	}
	/**
	 * Gibt alle ausgehenden Kanten des angegebenen Knotens zurück.
	 * 
	 * @param node
	 *            Der Knoten, dessen ausgehende Kanten gesucht werden.
	 * @return
	 */
	public Set<Integer> getOutgoingEdges(int node) {
		return null;
	}
	/**
	 * Gibt eine geometrische Datenstruktur zur angegebenen Zoomstufe zurück.
	 * 
	 * @param zoom
	 *            Die Zoomstufe.
	 * @return
	 */
	public GraphIndex getIndex(int zoom) {
		return null;
	}

	/**
	 * Gibt alle in den Knoten eingehende Kanten zurück.
	 * 
	 * @param node
	 *            Der Knoten, dessen eingehende Kanten gesucht werden.
	 * @return
	 */
	public Set<Integer> getIncomingEdges(int node) {
		return null;
	}
	/**
	 * Gibt die {@code EdgeProperties} der angegebenen Kante zurück.
	 * 
	 * @param edge
	 *            Die Kante, deren {@link EdgeProperties} gesucht werden.
	 * @return
	 */
	public EdgeProperties getEdgeProperties(int edge) {
		return edgeProps[edge];
	}
	/**
	 * Gibt den Endknoten der angegebenen Kante zurück.
	 * 
	 * @param edge
	 *            Die Kante, dessen Endknoten gesucht wird.
	 * @return
	 */
	public int getTargetNode(int edge) {
		return edges[edge];
	}
}