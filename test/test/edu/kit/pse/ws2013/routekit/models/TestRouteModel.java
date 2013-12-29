package test.edu.kit.pse.ws2013.routekit.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import edu.kit.pse.ws2013.routekit.models.RouteModel;
import edu.kit.pse.ws2013.routekit.models.RouteModelListener;
import edu.kit.pse.ws2013.routekit.routecalculation.Route;
import edu.kit.pse.ws2013.routekit.routecalculation.RouteDescription;
import edu.kit.pse.ws2013.routekit.util.Coordinates;

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

	@Test
	public void testCurrentRoute() {
		Route r1 = new Route(null, null, null, null);
		Route r2 = new Route(null, null, null, null);
		assertNotSame(r1, r2); // Sanity check

		assertEquals(0, listenerFired);
		assertNull(rm.getCurrentRoute());
		assertEquals(0, listenerFired);
		rm.setCurrentRoute(r1);
		assertEquals(1, listenerFired);
		assertSame(r1, rm.getCurrentRoute());
		assertEquals(1, listenerFired);
		rm.setCurrentRoute(r2);
		assertEquals(2, listenerFired);
		assertSame(r2, rm.getCurrentRoute());
		assertEquals(2, listenerFired);
	}

	@Test
	public void testStartAndDestination() {
		Coordinates c1 = new Coordinates(10, 10);
		Coordinates c2 = new Coordinates(11, 11);
		assertNotSame(c1, c2); // Sanity check

		assertEquals(0, listenerFired);
		assertNull(rm.getStart());
		assertEquals(0, listenerFired);
		assertNull(rm.getDestination());
		assertEquals(0, listenerFired);

		rm.setStart(c1);
		assertEquals(1, listenerFired);
		assertSame(c1, rm.getStart());
		assertEquals(1, listenerFired);

		rm.setDestination(c2);
		assertEquals(2, listenerFired);
		assertSame(c2, rm.getDestination());
		assertEquals(2, listenerFired);

		rm.setStart(c2);
		assertEquals(3, listenerFired);
		assertSame(c2, rm.getStart());
		assertEquals(3, listenerFired);
	}
	@Test
	public void testMultipleListeners() {
		rm.addRouteListener(new DummyListener());
		Coordinates c1 = new Coordinates(10, 10);
		Coordinates c2 = new Coordinates(11, 11);

		assertEquals(0, listenerFired);

		rm.setStart(c1);
		assertEquals(2, listenerFired);

		rm.setDestination(c2);
		assertEquals(4, listenerFired);

	}

}
