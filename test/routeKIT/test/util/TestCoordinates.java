package routeKIT.test.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import edu.kit.pse.ws2013.routekit.util.Coordinates;

public class TestCoordinates {
	public static final double EPSILON = 0.00001;

	@Before
	public void setUp() throws Exception {
	}

	@Ignore
	@Test
	public void testAngleBetween() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testDistanceTo() {
		fail("Not yet implemented");
	}

	@Test
	public void testFromSmt() {
		float a = 189237;
		float b = 128346;
		Coordinates c = Coordinates.fromSmt(a, b, 10);
		a %= 1 << 10;
		b %= 1 << 10;
		assertEquals(a, c.getSmtX(10), EPSILON);
		assertEquals(b, c.getSmtY(10), EPSILON);
	}
	@Test
	public void testToSmt() {
		float a = 50;
		float b = 35;
		Coordinates c = new Coordinates(a, b);
		Coordinates c2 = Coordinates.fromSmt(c.getSmtX(14), c.getSmtY(14), 14);
		assertEquals(c.getLat(), c2.getLat(),EPSILON);
		assertEquals(c.getLon(), c2.getLon(),EPSILON);
	}

}
