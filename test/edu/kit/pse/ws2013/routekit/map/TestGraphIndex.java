package edu.kit.pse.ws2013.routekit.map;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.junit.Test;

import edu.kit.pse.ws2013.routekit.util.Coordinates;

public class TestGraphIndex {
	@Test
	public void test() {
		int[] nodes = new int[400];
		int[] edges = new int[200];
		float[] lat = new float[400];
		float[] lon = new float[400];
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 10; j++) {
				nodes[i * 20 + j * 2] = i * 10 + j;
				nodes[i * 20 + j * 2 + 1] = i * 10 + j + 1;
				edges[i * 10 + j] = i * 20 + j * 2 + 1;
				lat[i * 20 + j * 2] = i;
				lon[i * 20 + j * 2] = j * 2;
				lat[i * 20 + j * 2 + 1] = i;
				lon[i * 20 + j * 2 + 1] = j * 2 + 1;
			}
		}
		EdgeProperties[] edgeProps = new EdgeProperties[edges.length];
		Arrays.fill(edgeProps, new EdgeProperties(HighwayType.Unclassified,
				"Test", null, 50));
		Graph g = new Graph(nodes, edges,
				new HashMap<Integer, NodeProperties>(), edgeProps, lat, lon);
		GraphIndex i = g.getIndex(0);
		Set<Integer> set = i.getEdgesInRectangle(new Coordinates(3.5f, 4.5f),
				new Coordinates(4.5f, 5.5f));
		System.out.println(set);
	}
}
