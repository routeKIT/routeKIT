package edu.kit.pse.ws2013.routekit.precalculation;

import edu.kit.pse.ws2013.routekit.map.EdgeBasedGraph;
import edu.kit.pse.ws2013.routekit.models.ArcFlags;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;

/**
 * Calculates the {@link ArcFlags} of the partitioned, weighted
 * {@link EdgeBasedGraph} in a {@link ProfileMapCombination}.
 * 
 * @author Fabian Hafner
 * @version 1.0
 * 
 */
public interface ArcFlagsCalculator {
	/**
	 * Calculates and sets the {@link ArcFlags} of the given weighted
	 * {@link ProfileMapCombination}.
	 * 
	 * @param combination
	 *            a weighted {@link ProfileMapCombination}
	 */
	public void calculateArcFlags(ProfileMapCombination combination);
}
