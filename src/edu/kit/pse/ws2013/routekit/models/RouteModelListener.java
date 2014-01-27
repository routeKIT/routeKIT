package edu.kit.pse.ws2013.routekit.models;

/**
 * Listens for changes in the {@link RouteModel}.
 */
public interface RouteModelListener {
	/**
	 * Called for each change in the {@link RouteModel}.
	 */
	public void routeModelChanged();
}
