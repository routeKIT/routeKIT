package edu.kit.pse.ws2013.routekit.precalculation;

import edu.kit.pse.ws2013.routekit.models.ArcFlags;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;

public class ArcFlagsCalculatorDummy implements ArcFlagsCalculator {

	@Override
	public void calculateArcFlags(ProfileMapCombination combination) {
		long t1 = System.currentTimeMillis();
		int[] flagsArray = new int[combination.getStreetMap()
				.getEdgeBasedGraph().getNumberOfTurns()];
		for (int i = 0; i < flagsArray.length; i++) {
			flagsArray[i] = 0xFFFFFFFF;
		}
		long t2 = System.currentTimeMillis();
		int time = (int) (t2 - t1);
		combination.setArcFlags(new ArcFlags(flagsArray), time);
	}

}
