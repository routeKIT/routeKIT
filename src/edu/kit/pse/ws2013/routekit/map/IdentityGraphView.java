package edu.kit.pse.ws2013.routekit.map;

public class IdentityGraphView implements GraphView {
	Graph g;

	public IdentityGraphView(Graph g) {
		this.g = g;
	}

	@Override
	public int getNumberOfEdges() {
		return g.getNumberOfEdges();
	}

	@Override
	public int getStartNode(int edge) {
		return g.getStartNode(edge);
	}

	@Override
	public int getTargetNode(int edge) {
		return g.getTargetNode(edge);
	}

	@Override
	public int translate(int edg) {
		return edg;
	}

}
