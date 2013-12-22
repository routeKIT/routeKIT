package edu.kit.pse.ws2013.routekit.models;

/**
 * Wird bei Änderungen am {@link RouteModel} informiert.
 */
public interface RouteModelListener {
	/**
	 * Wird bei jeder Änderung am {@link RouteModel} aufgerufen.
	 */
	public void routeModelChanged();
}
