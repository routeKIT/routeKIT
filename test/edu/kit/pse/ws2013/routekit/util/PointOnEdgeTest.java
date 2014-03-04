package edu.kit.pse.ws2013.routekit.util;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class PointOnEdgeTest {
	private static PointOnEdge pointOnEdge;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		pointOnEdge = new PointOnEdge(42, 0.5F);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorNegativeEdge() {
		new PointOnEdge(-1, 0.0F);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorPositionOutOfBoundsNegative() {
		new PointOnEdge(1, -0.4F);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorPositionOutOfBoundsPositive() {
		new PointOnEdge(1, 1.2F);
	}

	@Test
	public void testGetEdge() {
		assertEquals(42, pointOnEdge.getEdge());
	}

	@Test
	public void testGetPosition() {
		assertEquals(0.5F, pointOnEdge.getPosition(), 0);
	}
}
