package edu.kit.pse.ws2013.routekit.map;

public interface GraphView {
	public int getNumberOfEdges();

	public int getStartNode(int edge);

	public int getTargetNode(int edge);

	public int translate(int edg);
}
