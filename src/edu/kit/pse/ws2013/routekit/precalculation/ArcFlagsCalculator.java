package edu.kit.pse.ws2013.routekit.precalculation;
import edu.kit.pse.ws2013.routekit.models.ArcFlags;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
/**
 * Berechnet Arc-Flags für einen partitionierten, gewichteten Graphen.
 */
public interface ArcFlagsCalculator {
	/**
	 * Berechnet die Arc-Flags für die angegebene Kombination und setzt die
	 * {@link ArcFlags} von {@code combination} entsprechend.
	 * 
	 * @param combination
	 *            Die Kombination aus Profil und Karte.
	 */
	public void calculateArcFlags(ProfileMapCombination combination);
}
