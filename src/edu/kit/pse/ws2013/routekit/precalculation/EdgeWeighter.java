package edu.kit.pse.ws2013.routekit.precalculation;
import edu.kit.pse.ws2013.routekit.map.EdgeBasedGraph;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.Weights;
/**
 * Versieht den kantenbasierten Graphen ({@link EdgeBasedGraph}) mit
 * Kantengewichten.
 * 
 * Die Kantengewichte des kantenbasierten Graphen sind profilabhängig und geben
 * an, wie „teuer“ ein bestimmter Abbiegevorgang ist – Kanten mit niedrigerem
 * Gewicht werden bei der Routenberechnung bevorzugt gewählt.
 * Abbiegebeschränkungen werden durch maximale Kantengewichte umgesetzt.
 */
public interface EdgeWeighter {
	/**
	 * Berechnet die Kantengewichte für die angegebene Kombination und setzt die
	 * {@link Weights} von {@code combination} entsprechend.
	 * 
	 * @param combination
	 *            Die zu gewichtende Kombination aus Profil und Karte.
	 */
	public void weightEdges(ProfileMapCombination combination);
}
