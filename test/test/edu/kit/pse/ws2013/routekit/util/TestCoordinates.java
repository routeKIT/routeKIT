package test.edu.kit.pse.ws2013.routekit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import edu.kit.pse.ws2013.routekit.util.Coordinates;

public class TestCoordinates {
	public static final double EPSILON = 0.00001;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAngleBetween() {
		Coordinates equator1 = new Coordinates(0, 0);
		Coordinates equator2 = new Coordinates(0, 120);
		Coordinates equator3 = new Coordinates(0, -120);
		Coordinates northPole = new Coordinates(90, 0);
		Coordinates southPole = new Coordinates(-90, 0);
		Coordinates greenwich = new Coordinates(51.477778f, 0);
		assertEquals(180, equator1.angleBetween(equator2, equator3), EPSILON);
		assertEquals(180, equator1.angleBetween(northPole, southPole), EPSILON);
		assertEquals(270, equator1.angleBetween(northPole, equator2), EPSILON);
		assertEquals(90, equator1.angleBetween(northPole, equator3), EPSILON);
		assertEquals(0, equator1.angleBetween(equator2, equator2), EPSILON);
		assertEquals(0, Math.IEEEremainder(
				equator1.angleBetween(greenwich, greenwich), 360), EPSILON);
		assertEquals(0, Math.IEEEremainder(
				equator1.angleBetween(northPole, northPole), 360), EPSILON);
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
		Coordinates c1 = new Coordinates(a, b);
		Coordinates c2 = Coordinates
				.fromSmt(c1.getSmtX(14), c1.getSmtY(14), 14);
		assertCoordinatesEquals(c1, c2);
	}

	@Test
	public void testToFromString() {
		float lat = 49.013766f;
		float lon = 8.419944f;
		Coordinates c1 = new Coordinates(lat, lon);
		Coordinates c2 = Coordinates.fromString(c1.toString());
		assertCoordinatesEquals(c1, c2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromStringFail_null() {
		Coordinates.fromString(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromStringFail_oneCoordinate() {
		Coordinates.fromString("42");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromStringFail_threeCoordinates() {
		Coordinates.fromString("11 38 42");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromStringFail_latNonNumeric() {
		Coordinates.fromString("bogus 42");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromStringFail_latNotDisplayable() {
		Coordinates.fromString("86 42");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromStringFail_latBarelyNotDisplayable() {
		Coordinates.fromString("85.0000000000000000000000000001 42");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromStringFail_lonNonNumeric() {
		Coordinates.fromString("42 bogus");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromStringFail_lonNotOnEarth() {
		Coordinates.fromString("42 -182");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromStringFail_lonBarelyNotOnEarth() {
		Coordinates.fromString("42 -180.00000000000000000000000001");
	}

	public static void assertCoordinatesEquals(Coordinates expected,
			Coordinates actual) {
		assertEquals(expected.getLatitude(), actual.getLatitude(), EPSILON);
		assertEquals(expected.getLongitude(), actual.getLongitude(), EPSILON);
	}
}
