package routeKIT.test.profiles;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.profiles.VehicleType;

@SuppressWarnings("static-method")
public class TestProfile {
	
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

	@Test
	public void testClone() {
		assertTrue(Profile.defaultCar.clone().equals(Profile.defaultCar, false));
		assertTrue(Profile.defaultTruck.clone().equals(Profile.defaultTruck, false));
		
		assertTrue(Profile.defaultCar.isDefault());
		assertFalse(Profile.defaultCar.clone().isDefault());
	}
	
	@Test(expected = IllegalStateException.class)
	public void testName() {
		final Profile p = Profile.defaultCar.clone();
		final String testValue = "Test name";
		p.setName(testValue);
		assertEquals(p.getName(), testValue);
		Profile.defaultCar.setName(testValue); // should not be allowed
	}
	
	@Test(expected = IllegalStateException.class)
	public void testVehicleType() {
		final Profile p = Profile.defaultCar.clone();
		final VehicleType testValue = VehicleType.Motorcycle;
		p.setVehicleType(testValue);
		assertEquals(p.getVehicleType(), testValue);
		Profile.defaultCar.setVehicleType(testValue); // should not be allowed
	}
	
	@Test(expected = IllegalStateException.class)
	public void testHeight() {
		final Profile p = Profile.defaultCar.clone();
		final int testValue = 1;
		p.setHeight(testValue);
		assertEquals(p.getHeight(), testValue);
		Profile.defaultCar.setHeight(testValue); // should not be allowed
	}
	
	@Test(expected = IllegalStateException.class)
	public void testWidth() {
		final Profile p = Profile.defaultCar.clone();
		final int testValue = 1;
		p.setWidth(testValue);
		assertEquals(p.getWidth(), testValue);
		Profile.defaultCar.setWidth(testValue); // should not be allowed
	}
	
	@Test(expected = IllegalStateException.class)
	public void testWeight() {
		final Profile p = Profile.defaultCar.clone();
		final int testValue = 1;
		p.setWeight(testValue);
		assertEquals(p.getWeight(), testValue);
		Profile.defaultCar.setWeight(testValue); // should not be allowed
	}
	
	@Test(expected = IllegalStateException.class)
	public void testSpeedHighway() {
		final Profile p = Profile.defaultCar.clone();
		final int testValue = 1;
		p.setSpeedHighway(testValue);
		assertEquals(p.getSpeedHighway(), testValue);
		Profile.defaultCar.setSpeedHighway(testValue); // should not be allowed
	}
	
	@Test(expected = IllegalStateException.class)
	public void testSpeedRoad() {
		final Profile p = Profile.defaultCar.clone();
		final int testValue = 1;
		p.setSpeedRoad(testValue);
		assertEquals(p.getSpeedRoad(), testValue);
		Profile.defaultCar.setSpeedRoad(testValue); // should not be allowed
	}
	
	@Test
	public void testSaveLoad() throws IOException {
		final String name = "Test name";
		final VehicleType vehicleType = VehicleType.Bus;
		final int height = 300;
		final int width = 200;
		final int weight = 15000;
		final int speedHighway = 100;
		final int speedRoad = 80;
		final Profile p = new Profile(name, vehicleType, height, width, weight, speedHighway, speedRoad);
		File file = File.createTempFile("routeKit_testProfile_", ".properties");
		p.save(file);
		assertEquals(Profile.load(file), p);
	}
}
