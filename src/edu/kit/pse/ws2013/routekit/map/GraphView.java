package edu.kit.pse.ws2013.routekit.map;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class GraphView {
	public abstract int getNumberOfEdges();

	public abstract int getStartNode(int edge);

	public abstract int getTargetNode(int edge);

	public abstract int translate(int edg);

	public abstract void save(File file) throws IOException;

	public static GraphView load(Graph graph, File file) throws IOException {
		try (FileInputStream fis = new FileInputStream(file);
				DataInputStream dis = new DataInputStream(fis)) {
			int edgePos = dis.readInt();
			switch (edgePos) {
			case 0: {
				return new IdentityGraphView(graph);
			}
			default: {
				return ReducedGraphView.load(graph, file);
			}
			}
		}
	}
}
