package edu.kit.pse.ws2013.routekit.precalculation;

import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;

/**
 * Executes the precalculation for a given {@link ProfileMapCombination}.
 */
public class PreCalculator {
	EdgeWeighter weighter;
	ArcFlagsCalculator calulator;

	public PreCalculator() {
		weighter = new EdgeWeighterImpl();
		calulator = new ArcFlagsCalculatorDummy();
	}

	/**
	 * Executes the precalculation for the given {@link ProfileMapCombination}
	 * using an {@link EdgeWeighter} and an {@link ArcFlagsCalculator}.
	 * 
	 * @param comb
	 *            The {@link ProfileMapCombination} for which the precalculation
	 *            shall be executed.
	 * @param reporter
	 *            The {@link ProgressReporter} to which progress shall be
	 *            reported.
	 */
	public void doPrecalculation(ProfileMapCombination comb,
			ProgressReporter reporter) {
		reporter.setSubTasks(new float[] { .2f, .8f });
		reporter.pushTask("Berechne Kantengewichte");
		weighter.weightEdges(comb);
		reporter.nextTask("Berechne Arc-Flags");
		calulator.calculateArcFlags(comb);
		reporter.popTask();
	}
}
