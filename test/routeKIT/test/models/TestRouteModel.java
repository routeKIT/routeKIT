package routeKIT.test.models;

import static org.junit.Assert.*;
import models.RouteModel;
import models.RouteModelListener;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import routeCalculation.RouteDescription;

public class TestRouteModel {
	int listenerFired = 0;
	class DummyListener implements RouteModelListener {

		@Override
		public void routeModelChanged() {
			listenerFired++;
		}

	}
	RouteModel rm;

	@Before
	public void setUp() throws Exception {
		rm = new RouteModel();
		rm.addRouteListener(new DummyListener());
	}

	@Test
	public void testCurrentDescription() {
		RouteDescription rd1 = new RouteDescription(null, null);
		RouteDescription rd2 = new RouteDescription(null, null);
		assertNotSame(rd1, rd2); // Sanity check

		assertEquals(0, listenerFired);
		assertNull(rm.getCurrentDescription());
		assertEquals(0, listenerFired);
		rm.setCurrentDescription(rd1);
		assertEquals(1, listenerFired);
		assertSame(rd1, rm.getCurrentDescription());
		assertEquals(1, listenerFired);
		rm.setCurrentDescription(rd2);
		assertEquals(2, listenerFired);
		assertSame(rd2, rm.getCurrentDescription());
		assertEquals(2, listenerFired);
	}


	@Ignore
	@Test
	public void testCurrentRoute() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testDestination() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testStart() {
		fail("Not yet implemented");
	}

}
