package edu.kit.pse.ws2013.routekit.map;

/**
 * Eine Karte.
 */
public class StreetMap {
	private String name;
	private Graph graph;
	private EdgeBasedGraph edgeBasedGraph;

	/**
	 * Gibt zurück, ob es sich um eine Standardkarte handelt.
	 * 
	 * @return
	 */
	public boolean isDefault() {
		return false;
	}

	/**
	 * Konstruktor: Erzeugt ein neues Objekt aus den gegebenen Graphen.
	 * 
	 * @param graph
	 *            Der Kartengraph.
	 * @param edgeBasedGraph
	 *            Der kantenbasierte Graph.
	 */
	public StreetMap(Graph graph, EdgeBasedGraph edgeBasedGraph) {
		this.graph = graph;
		this.edgeBasedGraph = edgeBasedGraph;
	}

	public EdgeBasedGraph getEdgeBasedGraph() {
		return edgeBasedGraph;
	}
	public Graph getGraph() {
		return graph;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
