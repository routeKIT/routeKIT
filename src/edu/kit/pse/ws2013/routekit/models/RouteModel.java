package edu.kit.pse.ws2013.routekit.models;

import java.util.LinkedList;

import edu.kit.pse.ws2013.routekit.routecalculation.Route;
import edu.kit.pse.ws2013.routekit.routecalculation.RouteDescription;
import edu.kit.pse.ws2013.routekit.util.Coordinates;

/**
 * Stores the current start and destination point as well as the current route.
 * The getters return {@code null} if no such element is currently available;
 * the setters notify registered {@link RouteModelListener}.
 */
public class RouteModel {

	private LinkedList<RouteModelListener> listeners = new LinkedList<>();
	private RouteDescription currentDescription;
	private Route currentRoute;
	private Coordinates start;
	private Coordinates destination;

	public RouteDescription getCurrentDescription() {
		return currentDescription;
	}

	public Route getCurrentRoute() {
		return currentRoute;
	}

	public Coordinates getDestination() {
		return destination;
	}

	public Coordinates getStart() {
		return start;
	}

	public void setCurrentDescription(RouteDescription currentDescription) {
		this.currentDescription = currentDescription;
		fireListeners();
	}

	public void setCurrentRoute(Route currentRoute) {
		this.currentRoute = currentRoute;
		fireListeners();
	}

	public void setDestination(Coordinates destination) {
		this.destination = destination;
		fireListeners();
	}

	public void setStart(Coordinates start) {
		this.start = start;
		fireListeners();
	}

	private void fireListeners() {
		for (RouteModelListener list : listeners) {
			list.routeModelChanged();
		}
	}

	/**
	 * Registers a {@link RouteModelListener} that is notified when the start or
	 * destination point or the current route changes.
	 * 
	 * @param listener
	 *            The new listener that shall be notified of changes.
	 */
	public void addRouteListener(RouteModelListener listener) {
		listeners.add(listener);
	}
}
