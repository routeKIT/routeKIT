package routeKIT.test.profiles;

import static org.junit.Assert.*;

import org.junit.Test;

import profiles.Profile;
import profiles.VehicleType;

public class TestProfile {
	
	@SuppressWarnings("static-method")
	@Test
	public void testEquals() {
		assertTrue(Profile.defaultCar.equals(Profile.defaultCar));
		assertTrue(Profile.defaultTruck.equals(Profile.defaultTruck));
		assertTrue(Profile.defaultCar.equals(Profile.defaultCar, true));
		assertTrue(Profile.defaultTruck.equals(Profile.defaultTruck, true));
		assertTrue(Profile.defaultCar.equals(Profile.defaultCar, false));
		assertTrue(Profile.defaultTruck.equals(Profile.defaultTruck, false));
		
		assertFalse(Profile.defaultCar.equals("nope"));
		assertFalse(Profile.defaultCar.equals(Profile.defaultTruck));
		assertFalse(Profile.defaultCar.equals(Profile.defaultTruck, false));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testClone() {
		assertTrue(Profile.defaultCar.clone().equals(Profile.defaultCar, false));
		assertTrue(Profile.defaultTruck.clone().equals(Profile.defaultTruck, false));
		
		assertTrue(Profile.defaultCar.isDefault());
		assertFalse(Profile.defaultCar.clone().isDefault());
	}
	
	@SuppressWarnings("static-method")
	@Test(expected = IllegalStateException.class)
	public void testName() {
		final Profile p = Profile.defaultCar.clone();
		final String testValue = "Test name";
		p.setName(testValue);
		assertEquals(p.getName(), testValue);
		Profile.defaultCar.setName(testValue); // should not be allowed
	}
	
	@SuppressWarnings("static-method")
	@Test(expected = IllegalStateException.class)
	public void testVehicleType() {
		final Profile p = Profile.defaultCar.clone();
		final VehicleType testValue = VehicleType.Motorcycle;
		p.setVehicleType(testValue);
		assertEquals(p.getVehicleType(), testValue);
		Profile.defaultCar.setVehicleType(testValue); // should not be allowed
	}
	
	@SuppressWarnings("static-method")
	@Test(expected = IllegalStateException.class)
	public void testHeight() {
		final Profile p = Profile.defaultCar.clone();
		final int testValue = 1;
		p.setHeight(testValue);
		assertEquals(p.getHeight(), testValue);
		Profile.defaultCar.setHeight(testValue); // should not be allowed
	}
	
	@SuppressWarnings("static-method")
	@Test(expected = IllegalStateException.class)
	public void testWidth() {
		final Profile p = Profile.defaultCar.clone();
		final int testValue = 1;
		p.setWidth(testValue);
		assertEquals(p.getWidth(), testValue);
		Profile.defaultCar.setWidth(testValue); // should not be allowed
	}
	
	@SuppressWarnings("static-method")
	@Test(expected = IllegalStateException.class)
	public void testWeight() {
		final Profile p = Profile.defaultCar.clone();
		final int testValue = 1;
		p.setWeight(testValue);
		assertEquals(p.getWeight(), testValue);
		Profile.defaultCar.setWeight(testValue); // should not be allowed
	}
	
	@SuppressWarnings("static-method")
	@Test(expected = IllegalStateException.class)
	public void testSpeedHighway() {
		final Profile p = Profile.defaultCar.clone();
		final int testValue = 1;
		p.setSpeedHighway(testValue);
		assertEquals(p.getSpeedHighway(), testValue);
		Profile.defaultCar.setSpeedHighway(testValue); // should not be allowed
	}
	
	@SuppressWarnings("static-method")
	@Test(expected = IllegalStateException.class)
	public void testSpeedRoad() {
		final Profile p = Profile.defaultCar.clone();
		final int testValue = 1;
		p.setSpeedRoad(testValue);
		assertEquals(p.getSpeedRoad(), testValue);
		Profile.defaultCar.setSpeedRoad(testValue); // should not be allowed
	}
}
