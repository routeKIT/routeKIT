package edu.kit.pse.ws2013.routekit.routecalculation;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;
/**
 * Stellt ein Interface für einen Algorithmus zur Routenberechnung bereit.
 */
public interface RouteCalculator {
	/**
	 * Berechnet einen Weg vom Startpunkt zum Zielpunkt auf dem gegebenen
	 * Graphen.
	 * 
	 * @param start
	 *            Der Startpunkt für die Routenberechnung.
	 * @param destination
	 *            Der Zielpunkt für die Routenberechnung.
	 * @param data
	 *            Der vorberechnete Graph auf dem die Routenberechnung
	 *            durchgeführt wird.
	 * @return
	 */
	public Route calculateRoute(PointOnEdge start, PointOnEdge destination,
			ProfileMapCombination data);
}
