package test.edu.kit.pse.ws2013.routekit.map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import edu.kit.pse.ws2013.routekit.map.EdgeProperties;
import edu.kit.pse.ws2013.routekit.map.Graph;
import edu.kit.pse.ws2013.routekit.map.HighwayType;
import edu.kit.pse.ws2013.routekit.map.NodeProperties;
import edu.kit.pse.ws2013.routekit.util.DummyProgressReporter;

public class TestGraph {
	public static final double EPSILON = 0.00001;
	Graph g;

	@Before
	public void setUp() throws Exception {
		int[] nodes = new int[] { 0, 3, 6, 6 };
		int[] edges = new int[] { 1, 2, 3, 0, 2, 3, 0, 1, 2 };
		HashMap<Integer, NodeProperties> nodeProps = new HashMap<>();
		EdgeProperties[] edgeProps = new EdgeProperties[edges.length];
		Arrays.fill(edgeProps, new EdgeProperties(HighwayType.Tertiary,
				"Bogus Str.", null, 30));
		float[] lat = new float[] { 0, 0, 1, 1 };
		float[] lon = new float[] { 0, 1, 0, 1 };
		g = new Graph(nodes, edges, nodeProps, edgeProps, lat, lon);
	}

	@Ignore
	@Test
	public void testGetNodeProperties() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCoordinates() {
		assertEquals(0, g.getCoordinates(1).getLatitude(), EPSILON);
		assertEquals(1, g.getCoordinates(1).getLongitude(), EPSILON);
	}

	@Test
	public void testGetStartNode() {
		assertEquals(0, g.getStartNode(0));
		assertEquals(0, g.getStartNode(1));
		assertEquals(0, g.getStartNode(2));
		assertEquals(1, g.getStartNode(3));
		assertEquals(1, g.getStartNode(4));
		assertEquals(1, g.getStartNode(5));
		assertEquals(3, g.getStartNode(6));
	}

	@Test
	public void testGetTargetNode() {
		assertEquals(1, g.getTargetNode(0));
		assertEquals(2, g.getTargetNode(1));
		assertEquals(3, g.getTargetNode(2));
	}

	@Test
	public void testGetOutgoing() {
		Integer[] data = g.getOutgoingEdges(0).toArray(new Integer[0]);
		assertArrayEquals(new Integer[] { 0, 1, 2 }, data);
		data = g.getOutgoingEdges(1).toArray(new Integer[0]);
		assertArrayEquals(new Integer[] { 3, 4, 5 }, data);
		data = g.getOutgoingEdges(2).toArray(new Integer[0]);
		assertArrayEquals(new Integer[] {}, data);
		data = g.getOutgoingEdges(3).toArray(new Integer[0]);
		assertArrayEquals(new Integer[] { 6, 7, 8 }, data);
	}

	@Ignore
	@Test
	public void testGetEdgeProperties() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCorresponding() {
		int[] corres = new int[] { 3, -1, 6, 0, -1, 7, 2, 5, -1 };
		for (int i = 0; i < corres.length; i++) {
			assertEquals("index: " + i, corres[i], g.getCorrespondingEdge(i));
		}
	}

	@Test
	public void testSaveLoad() throws IOException {
		File f = File.createTempFile("routeKit_testGraph_", ".graph");
		g.save(f);
		Graph loaded = Graph.load(f, new DummyProgressReporter());
		assertEquals(1, loaded.getTargetNode(0));
		assertEquals(2, loaded.getTargetNode(1));
		assertEquals(3, loaded.getTargetNode(2));
		g = loaded;
		testGetOutgoing();

		// TODO test lats, lons, {Node,Edge}Properties
	}

}
