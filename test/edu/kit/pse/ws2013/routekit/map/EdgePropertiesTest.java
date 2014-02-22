package edu.kit.pse.ws2013.routekit.map;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.pse.ws2013.routekit.profiles.Profile;

public class EdgePropertiesTest {
	private static EdgeProperties props;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		props = new EdgeProperties(HighwayType.Primary, "Hauptstraße", "B 42",
				80);
	}

	@Test
	public void testGetType() {
		assertEquals(HighwayType.Primary, props.getType());
	}

	@Test
	public void testGetName() {
		assertEquals("Hauptstraße", props.getName());
	}

	@Test
	public void testGetRoadRef() {
		assertEquals("B 42", props.getRoadRef());
	}

	@Test
	public void testGetMaxSpeed() {
		assertEquals(80, props.getMaxSpeed(Profile.defaultCar));
	}

	@Test
	public void testGetMaxSpeedDefault() {
		Profile car = Profile.defaultCar;
		assertEquals(car.getSpeedRoad(), new EdgeProperties(
				HighwayType.Primary, null, null, 0).getMaxSpeed(car));
		assertEquals(car.getSpeedHighway(), new EdgeProperties(
				HighwayType.Motorway, null, null, 0).getMaxSpeed(car));
		Profile truck = Profile.defaultTruck;
		assertEquals(truck.getSpeedHighway(), new EdgeProperties(
				HighwayType.Motorway, null, null, 120).getMaxSpeed(truck));
	}
}
