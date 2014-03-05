package edu.kit.pse.ws2013.routekit.map;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class IdentityGraphView extends GraphView {
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

	@Override
	public void save(File file) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(file);
				DataOutputStream dos = new DataOutputStream(fos)) {
			dos.writeInt(0);
		}
	}
}
