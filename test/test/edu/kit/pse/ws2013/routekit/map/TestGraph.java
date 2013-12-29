package test.edu.kit.pse.ws2013.routekit.map;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import edu.kit.pse.ws2013.routekit.map.EdgeProperties;
import edu.kit.pse.ws2013.routekit.map.Graph;
import edu.kit.pse.ws2013.routekit.map.NodeProperties;

public class TestGraph {
	public static final double EPSILON = 0.00001;
	Graph g;
	@Before
	public void setUp() throws Exception {
		g = new Graph(new int[]{0, 3, 6, 6}, new int[]{1, 2, 3, 0, 2, 3, 0, 1,
				2}, new HashMap<Integer, NodeProperties>(),
				new EdgeProperties[]{}, new float[]{0, 0, 1, 1}, new float[]{0,
						1, 0, 1});
	}

	@Ignore
	@Test
	public void testGetNodeProperties() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCoordinates() {
		assertEquals(0, g.getCoordinates(1).getLat(), EPSILON);
		assertEquals(1, g.getCoordinates(1).getLon(), EPSILON);
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

	@Ignore
	@Test
	public void testGetEdgeProperties() {
		fail("Not yet implemented");
	}

}
