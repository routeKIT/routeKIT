package edu.kit.pse.ws2013.routekit.map;

/**
 * Eine Karte.
 */
public class StreetMap {
	/**
	 * Gibt zurück, ob es sich um eine Standardkarte handelt.
	 * 
	 * @return
	 */
	private String name;

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
	}

	public EdgeBasedGraph getEdgeBasedGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
