package edu.kit.pse.ws2013.routekit.precalculation;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TurnRestrictionTest {
	private static MapEdge to1;
	private static MapEdge to2;
	private TurnRestriction noTurn;
	private TurnRestriction onlyTurn;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		OSMWay way = new OSMWay(Collections.<String, String> emptyMap());
		to1 = new MapEdge(1, way);
		to2 = new MapEdge(2, way);
	}

	@Before
	public void setUp() throws Exception {
		noTurn = new TurnRestriction(42, to1, false);
		onlyTurn = new TurnRestriction(42, to2, true);
	}

	@Test
	public void testGetFrom() {
		assertEquals(42, noTurn.getFrom());
		assertEquals(42, onlyTurn.getFrom());
	}

	@Test
	public void testGetTo() {
		assertSame(to1, noTurn.getTo());
		assertSame(to2, onlyTurn.getTo());
	}

	@Test
	public void testIsOnlyAllowedTurn() {
		assertFalse(noTurn.isOnlyAllowedTurn());
		assertTrue(onlyTurn.isOnlyAllowedTurn());
	}

	@Test
	public void testAllowsTo() {
		assertFalse(noTurn.allowsTo(to1));
		assertTrue(noTurn.allowsTo(to2));

		assertFalse(onlyTurn.allowsTo(to1));
		assertTrue(onlyTurn.allowsTo(to2));
	}
}
