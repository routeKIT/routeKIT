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
public class RouteModel implements CurrentCombinationListener {

	private LinkedList<RouteModelListener> listeners = new LinkedList<>();
	private RouteDescription currentDescription;
	private Route currentRoute;
	private Coordinates start;
	private Coordinates destination;
	private boolean isCalculating;

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

	/**
	 * Find out if a route calculation is currently taking place.
	 * 
	 * @return {@code true} if the route is being calculated, {@code false}
	 *         otherwise.
	 * @see #startCalculating()
	 */
	public boolean isCalculating() {
		return isCalculating;
	}

	/**
	 * Report the start of a route calculation. Route calculation is assumed to
	 * end on the next {@link #setCurrentRoute(Route)} call.
	 * 
	 * @see #isCalculating
	 */
	public void startCalculating() {
		isCalculating = true;
	}

	public void setCurrentDescription(RouteDescription currentDescription) {
		this.currentDescription = currentDescription;
		fireListeners();
	}

	/**
	 * Sets the current {@link Route}, and also invalidates the current
	 * {@link RouteDescription}.
	 * 
	 * @param currentRoute
	 *            The new current {@link Route}.
	 */
	public void setCurrentRoute(Route currentRoute) {
		this.currentRoute = currentRoute;
		isCalculating = false;
		setCurrentDescription(null);
	}

	/**
	 * Sets the current destination point, and also invalidates the current
	 * {@link Route}.
	 * 
	 * @param destination
	 *            The new destination point.
	 */
	public void setDestination(Coordinates destination) {
		this.destination = destination;
		setCurrentRoute(null);
	}

	/**
	 * Sets the current start point, and also invalidates the current
	 * {@link Route}.
	 * 
	 * @param start
	 *            The new start point.
	 */
	public void setStart(Coordinates start) {
		this.start = start;
		setCurrentRoute(null);
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

	@Override
	public void currentCombinationChanged(ProfileMapCombination newCombination) {
		this.start = null; // don’t use the setter to avoid firing event twice
		setDestination(null);
	}
}
