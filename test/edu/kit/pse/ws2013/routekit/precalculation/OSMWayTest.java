package edu.kit.pse.ws2013.routekit.precalculation;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import edu.kit.pse.ws2013.routekit.map.EdgeProperties;
import edu.kit.pse.ws2013.routekit.map.HeightRestriction;
import edu.kit.pse.ws2013.routekit.map.HighwayType;
import edu.kit.pse.ws2013.routekit.map.MultipleRestrictions;
import edu.kit.pse.ws2013.routekit.map.Restriction;
import edu.kit.pse.ws2013.routekit.map.VehicleTypeRestriction;
import edu.kit.pse.ws2013.routekit.map.WeightRestriction;
import edu.kit.pse.ws2013.routekit.map.WidthRestriction;
import edu.kit.pse.ws2013.routekit.profiles.VehicleType;

public class OSMWayTest {
	private OSMWay dummyWay;

	private OSMWay getHighwayTest(String type) {
		return new OSMWay(Collections.singletonMap("highway", type));
	}

	@Before
	public void setUp() throws Exception {
		dummyWay = new OSMWay(Collections.<String, String> emptyMap());
	}

	@Test(expected = IllegalStateException.class)
	public void testGetId() {
		dummyWay.getId();
	}

	@Test
	public void testSetId() {
		dummyWay.setId(42);
		assertEquals(42, dummyWay.getId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetIdNegative() {
		dummyWay.setId(-2);
	}

	@Test
	public void testGetRestrictionsNone() {
		assertNull(dummyWay.getRestriction());
	}

	@Test
	public void testGetRestrictionMaxheight() {
		OSMWay way = new OSMWay(Collections.singletonMap("maxheight", "2.5"));
		assertEquals(HeightRestriction.getInstance(250), way.getRestriction());
		way = new OSMWay(Collections.singletonMap("maxheight", "2.5 m"));
		assertEquals(HeightRestriction.getInstance(250), way.getRestriction());
		way = new OSMWay(Collections.singletonMap("maxheight", "2.5 meters"));
		assertNull(way.getRestriction());
	}

	@Test
	public void testGetRestrictionMaxwidth() {
		OSMWay way = new OSMWay(Collections.singletonMap("maxwidth", "2.5"));
		assertEquals(WidthRestriction.getInstance(250), way.getRestriction());
		way = new OSMWay(Collections.singletonMap("maxwidth", "2.5 m"));
		assertEquals(WidthRestriction.getInstance(250), way.getRestriction());
		way = new OSMWay(Collections.singletonMap("maxwidth", "2.5 meters"));
		assertNull(way.getRestriction());
	}

	@Test
	public void testGetRestrictionMaxweight() {
		OSMWay way = new OSMWay(Collections.singletonMap("maxweight", "2.5"));
		assertEquals(WeightRestriction.getInstance(2500), way.getRestriction());
		way = new OSMWay(Collections.singletonMap("maxweight", "2.5 t"));
		assertEquals(WeightRestriction.getInstance(2500), way.getRestriction());
		way = new OSMWay(Collections.singletonMap("maxweight", "2500 kg"));
		assertEquals(WeightRestriction.getInstance(2500), way.getRestriction());
		way = new OSMWay(Collections.singletonMap("maxweight", "2.5 to"));
		assertNull(way.getRestriction());
	}

	@Test
	public void testGetRestrictionsVehicle() {
		OSMWay way = new OSMWay(Collections.singletonMap("motorcar", "no"));
		assertEquals(VehicleTypeRestriction.getInstance(VehicleType.Car),
				way.getRestriction());
		way = new OSMWay(Collections.singletonMap("motorcycle", "no"));
		assertEquals(
				VehicleTypeRestriction.getInstance(VehicleType.Motorcycle),
				way.getRestriction());
		way = new OSMWay(Collections.singletonMap("hgv", "no"));
		assertEquals(VehicleTypeRestriction.getInstance(VehicleType.Truck),
				way.getRestriction());
		way = new OSMWay(Collections.singletonMap("bus", "no"));
		assertEquals(VehicleTypeRestriction.getInstance(VehicleType.Bus),
				way.getRestriction());
		way = new OSMWay(Collections.singletonMap("motorcar", "yes"));
		assertNull(way.getRestriction());
	}

	@Test
	public void testGetRestrictionsMultiple() {
		Map<String, String> map = new HashMap<>(3);
		map.put("maxweight", "6");
		map.put("maxheight", "3.3");
		Set<Restriction> restrictions = new HashSet<>(2);
		restrictions.add(WeightRestriction.getInstance(6000));
		restrictions.add(HeightRestriction.getInstance(330));
		assertEquals(MultipleRestrictions.getInstance(restrictions),
				new OSMWay(map).getRestriction());

		for (VehicleType type : VehicleType.values()) {
			restrictions.add(VehicleTypeRestriction.getInstance(type));
		}
		for (String tagName : new String[] { "access", "vehicle",
				"motor_vehicle" }) {
			map.put(tagName, "no");
			assertEquals(MultipleRestrictions.getInstance(restrictions),
					new OSMWay(map).getRestriction());
			map.remove(tagName);
		}
		map.put("motor_vehicle", "destination");
		map.put("motorcycle", "yes");
		restrictions.remove(VehicleTypeRestriction
				.getInstance(VehicleType.Motorcycle));
		assertEquals(MultipleRestrictions.getInstance(restrictions),
				new OSMWay(map).getRestriction());
	}

	@Test
	public void testIsRoundabout() {
		assertTrue(new OSMWay(
				Collections.singletonMap("junction", "roundabout"))
				.isRoundabout());
	}

	@Test
	public void testIsNoRoundabout() {
		assertFalse(dummyWay.isRoundabout());
	}

	@Test
	public void testIsOneway() {
		assertTrue(new OSMWay(Collections.singletonMap("oneway", "yes"))
				.isOneway());
		assertTrue(new OSMWay(Collections.singletonMap("oneway", "1"))
				.isOneway());
		assertTrue(new OSMWay(Collections.singletonMap("oneway", "tRuE"))
				.isOneway());
		assertTrue(new OSMWay(
				Collections.singletonMap("junction", "roundabout")).isOneway());
		assertTrue(getHighwayTest("motorway").isOneway());
	}

	@Test
	public void testIsNotOneway() {
		assertFalse(dummyWay.isOneway());
		Map<String, String> map = new HashMap<>(2);
		map.put("junction", "roundabout");
		map.put("oneway", "no");
		assertFalse(new OSMWay(map).isOneway());
		map.put("oneway", "-1");
		assertFalse(new OSMWay(map).isOneway());
		map.put("oneway", "0");
		assertFalse(new OSMWay(map).isOneway());
		map.put("oneway", "FaLsE");
		assertFalse(new OSMWay(map).isOneway());
	}

	@Test
	public void testIsReversedOneway() {
		assertTrue(new OSMWay(Collections.singletonMap("oneway", "-1"))
				.isReversedOneway());
	}

	@Test
	public void testIsNotReversedOneway() {
		assertFalse(dummyWay.isReversedOneway());
		assertFalse(new OSMWay(Collections.singletonMap("oneway", "yes"))
				.isReversedOneway());
		assertFalse(new OSMWay(Collections.singletonMap("oneway", "no"))
				.isReversedOneway());
	}

	@Test
	public void testGetHighwayType() {
		assertEquals(HighwayType.Motorway, getHighwayTest("motorway")
				.getHighwayType());
		assertEquals(HighwayType.Motorway, getHighwayTest("motorway_link")
				.getHighwayType());
		assertEquals(HighwayType.Trunk, getHighwayTest("trunk")
				.getHighwayType());
		assertEquals(HighwayType.Trunk, getHighwayTest("trunk_link")
				.getHighwayType());
		assertEquals(HighwayType.Primary, getHighwayTest("primary")
				.getHighwayType());
		assertEquals(HighwayType.Primary, getHighwayTest("primary_link")
				.getHighwayType());
		assertEquals(HighwayType.Secondary, getHighwayTest("secondary")
				.getHighwayType());
		assertEquals(HighwayType.Secondary, getHighwayTest("secondary_link")
				.getHighwayType());
		assertEquals(HighwayType.Tertiary, getHighwayTest("tertiary")
				.getHighwayType());
		assertEquals(HighwayType.Tertiary, getHighwayTest("tertiary_link")
				.getHighwayType());
		assertEquals(HighwayType.Unclassified, getHighwayTest("unclassified")
				.getHighwayType());
		assertEquals(HighwayType.Unclassified, getHighwayTest("road")
				.getHighwayType());
		assertEquals(HighwayType.Residential, getHighwayTest("residential")
				.getHighwayType());
	}

	@Test
	public void testGetHighwayTypeNull() {
		assertNull(dummyWay.getHighwayType());
		assertNull(getHighwayTest("footway").getHighwayType());
	}

	@Test
	public void testIsHighwayLink() {
		assertTrue(getHighwayTest("motoway_link").isHighwayLink());
	}

	@Test
	public void testIsNoHighwayLink() {
		assertFalse(dummyWay.isHighwayLink());
		assertFalse(getHighwayTest("motorway").isHighwayLink());
	}

	@Test
	public void testGetEdgeProperties() {
		EdgeProperties expectedProps = new EdgeProperties(HighwayType.Primary,
				"Hauptstraße", "B 42", 60);
		Map<String, String> map = new HashMap<>(4);
		map.put("highway", "primary");
		map.put("name", "Hauptstraße");
		map.put("ref", "B 42");
		map.put("maxspeed", "60");
		OSMWay way = new OSMWay(map);
		EdgeProperties actualProps = way.getEdgeProperties();
		assertEquals(expectedProps, actualProps);
		assertSame("repeated invocations should return the same object",
				actualProps, way.getEdgeProperties());

		expectedProps = new EdgeProperties(HighwayType.Primary, null, null, 0);
		map.put("name", "");
		map.put("ref", "");
		map.put("maxspeed", "NaN");
		assertEquals(expectedProps, new OSMWay(map).getEdgeProperties());
		assertEquals(expectedProps, getHighwayTest("primary")
				.getEdgeProperties());
	}
}
