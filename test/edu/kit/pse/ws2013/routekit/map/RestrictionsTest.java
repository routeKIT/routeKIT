package edu.kit.pse.ws2013.routekit.map;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.profiles.VehicleType;

public class RestrictionsTest {
	private void test(Restriction restriction) {
		assertTrue(restriction.allows(Profile.defaultCar));
		assertFalse(restriction.allows(Profile.defaultTruck));
	}

	@Test
	public void testHeightRestriction() {
		test(HeightRestriction.getInstance(200));
	}

	@Test
	public void testWidthRestriction() {
		test(WidthRestriction.getInstance(200));
	}

	@Test
	public void testWeightRestriction() {
		test(WeightRestriction.getInstance(7500));
	}

	@Test
	public void testVehicleRestriction() {
		test(VehicleTypeRestriction.getInstance(VehicleType.Truck));
	}

	@Test
	public void testMultipleRestrictions() {
		test(MultipleRestrictions.getInstance(Arrays.asList((new Restriction[] {
				VehicleTypeRestriction.getInstance(VehicleType.Truck),
				WidthRestriction.getInstance(500) }))));
	}

	@Test
	public void testNoRestrictions() {
		assertTrue(NoRestriction.getInstance().allows(Profile.defaultTruck));
	}
}
