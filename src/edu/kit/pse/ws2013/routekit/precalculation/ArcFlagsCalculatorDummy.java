package edu.kit.pse.ws2013.routekit.precalculation;

import edu.kit.pse.ws2013.routekit.models.ArcFlags;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;

public class ArcFlagsCalculatorDummy implements ArcFlagsCalculator {

	@Override
	public void calculateArcFlags(ProfileMapCombination combination) {
		int[] flagsArray = new int[combination.getStreetMap()
				.getEdgeBasedGraph().getNumberOfTurns()];
		for (int i = 0; i < flagsArray.length; i++) {
			flagsArray[i] = Integer.MAX_VALUE;
		}
		combination.setArcFlags(new ArcFlags(flagsArray));
	}

}
