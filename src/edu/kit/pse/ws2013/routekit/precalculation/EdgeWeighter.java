package edu.kit.pse.ws2013.routekit.precalculation;

import edu.kit.pse.ws2013.routekit.map.EdgeBasedGraph;
import edu.kit.pse.ws2013.routekit.models.ArcFlags;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;
import edu.kit.pse.ws2013.routekit.models.Weights;

/**
 * Calculates and sets the {@link Weights} of a {@link ProfileMapCombination}'s
 * {@link EdgeBasedGraph}.
 * 
 * Restricted turns are marked by maximal weights.
 * 
 * @author Fabian Hafner
 * @version 1.0
 * 
 */
public interface EdgeWeighter {
	/**
	 * Calculates and sets the {@link Weights} of the given
	 * {@link ProfileMapCombination}.<br>
	 * <br>
	 * {@code combination}'s {@link ArcFlags} are set to <code>null</code> to
	 * prevent inconsistencies.
	 * 
	 * @param combination
	 *            a {@link ProfileMapCombination}
	 * @param reporter
	 *            The {@link ProgressReporter} to report progress to.
	 */
	public void weightEdges(ProfileMapCombination combination,
			ProgressReporter reporter);
}
