package edu.kit.pse.ws2013.routekit.map;
import edu.kit.pse.ws2013.routekit.util.Coordinates;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;
/**
 * Eine geometrische Datenstruktur zum schnellen Auffinden von Kanten innerhalb
 * eines Kartenausschnitts.
 */
public class GraphIndex {
	/**
	 * Sucht zu gegebenen Koordinaten den nächsten Punkt auf einer Kante.
	 * 
	 * @param coords
	 *            Die Koordinaten eines Punktes.
	 * @return
	 */
	public PointOnEdge findNearestPointOnEdge(Coordinates coords) {
		return null;
	}
	/**
	 * Bestimmt alle Kanten innerhalb eines rechteckigen Kartenausschnitts, der
	 * durch {@code leftTop} und {@code rightBottom} festgelegt ist.
	 * 
	 * @param leftTop
	 *            Die Koordinaten der linken oberen Ecke des Ausschnitts.
	 * @param rightBottom
	 *            Die Koordinaten der rechten unteren Ecke des Ausschnitts.
	 * @return
	 */
	public int[] getEdgesInRectangle(Coordinates leftTop,
			Coordinates rightBottom) {
		return null;
	}
	/**
	 * Konstruktor: Erzeugt die Datenstruktur für den gegebenen Graph und die
	 * angegebene Zoomstufe.
	 * 
	 * @param graph
	 *            Ein Graph.
	 * @param zoom
	 *            Die Zoomstufe.
	 */
	public GraphIndex(Graph graph, int zoom) {
	}
}
