package edu.kit.pse.ws2013.routekit.export;

import java.io.File;

import edu.kit.pse.ws2013.routekit.routecalculation.Route;

/**
 * Provides the functionality to export a route in the GPS Exchange Format
 * (GPX).
 */
public class GPXExporter {
	/**
	 * Exports the route points of the given route in the GPS Exchange Format
	 * (GPX) into the specified file.
	 * 
	 * @param route
	 *            the route to be exported
	 * @param file
	 *            the GPX file to write into
	 * @throws IllegalArgumentException
	 *             if {@code route} or {@code file} is {@code null}
	 */
	public void exportRoute(Route route, File file) {
		if (route == null || file == null) {
			throw new IllegalArgumentException();
		}
	}
}
