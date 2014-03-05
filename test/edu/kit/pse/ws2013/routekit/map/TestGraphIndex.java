package edu.kit.pse.ws2013.routekit.map;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import edu.kit.pse.ws2013.routekit.util.Coordinates;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;

public class TestGraphIndex {
	@Test
	public void test() {
		Graph g = generateTestGraph();
		GraphIndex i = g.getIndex(19);
		assertNormalBehavior(i);
	}

	@Test
	public void testTreeGraph() {
		Graph g = generateTestGraph();
		GraphIndex i = new TreeGraphIndex(g, HighwayType.Residential,
				new IdentityGraphView(g));
		assertNormalBehavior(i);
	}

	private void assertNormalBehavior(GraphIndex i) {
		Assert.assertArrayEquals(
				i.getEdgesInRectangle(new Coordinates(3.5f, 4.5f),
						new Coordinates(4.5f, 5.5f)).toArray(),
				new Object[] { 42 });

		Assert.assertArrayEquals(
				i.getEdgesInRectangle(new Coordinates(4.5f, 4.5f),
						new Coordinates(5.5f, 5.5f)).toArray(),
				new Object[] { 52 });
		Assert.assertArrayEquals(
				i.getEdgesInRectangle(new Coordinates(5.5f, 4.5f),
						new Coordinates(6.5f, 5.5f)).toArray(),
				new Object[] { 62 });
		Assert.assertArrayEquals(
				i.getEdgesInRectangle(new Coordinates(5.5f, 4.5f),
						new Coordinates(5.6f, 5.5f)).toArray(), new Object[] {});
		PointOnEdge edge = i
				.findNearestPointOnEdge(new Coordinates(5.95f, 4.5f));
		Assert.assertEquals(62, edge.getEdge());
		Assert.assertEquals(0.5f, edge.getPosition(), 0.0001f);
		Assert.assertNotNull(i.getView());

	}

	private Graph generateTestGraph() {
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
		return g;
	}

}
