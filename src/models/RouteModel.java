package models;
/**
 * Stellt die aktuellen Start- und Zielpunkte, sowie die aktuell berechnete
 * Route dar. Die Getter liefern dabei immer den aktuellen Zustand (auch
 * {@code null} möglich). Die Setter ändern den Wert und informieren eventuelle
 * {@link RouteListener}.
 */
public class RouteModel {
	/**
	 * Fügt einen RouteListener dem Modell hinzu, damit er über Änderungen an
	 * Route, Start oder Ziel informiert wird
	 * 
	 * @param listener
	 *            Der neue Listener, der über Änderungen informiert werden will.
	 */
	public void addRouteListener(RouteModelListener listener) {
	}
}
