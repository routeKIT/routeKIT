package edu.kit.pse.ws2013.routekit.models;

import java.util.LinkedList;

import edu.kit.pse.ws2013.routekit.routecalculation.Route;
import edu.kit.pse.ws2013.routekit.routecalculation.RouteDescription;
import edu.kit.pse.ws2013.routekit.util.Coordinates;

/**
 * Stellt die aktuellen Start- und Zielpunkte, sowie die aktuell berechnete
 * Route dar. Die Getter liefern dabei immer den aktuellen Zustand (auch
 * {@code null} möglich). Die Setter ändern den Wert und informieren eventuelle
 * {@link RouteListener}.
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
	 * Fügt einen RouteListener dem Modell hinzu, damit er über Änderungen an
	 * Route, Start oder Ziel informiert wird
	 * 
	 * @param listener
	 *            Der neue Listener, der über Änderungen informiert werden will.
	 */
	public void addRouteListener(RouteModelListener listener) {
		listeners.add(listener);
	}
}
