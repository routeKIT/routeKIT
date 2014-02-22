package edu.kit.pse.ws2013.routekit.precalculation;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MapEdgeTest {
	private static OSMWay way;
	private MapEdge edge;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		way = new OSMWay(Collections.<String, String> emptyMap());
	}

	@Before
	public void setUp() throws Exception {
		edge = new MapEdge(42, way);
	}

	@Test
	public void testGetTargetNode() {
		assertEquals(42, edge.getTargetNode());
	}

	@Test
	public void testGetWay() {
		assertSame(edge.getWay(), way);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetId() {
		edge.getId();
	}

	@Test
	public void testSetId() {
		edge.setId(0);
		assertEquals(0, edge.getId());
	}
}
