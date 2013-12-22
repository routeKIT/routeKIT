package edu.kit.pse.ws2013.routekit.precalculation;

import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.Weights;

public class EdgeWeighterDummy implements EdgeWeighter {

	@Override
	public void weightEdges(ProfileMapCombination combination) {
		int [] weightArray = new int[combination.getStreetMap().getEdgeBasedGraph().getNumberOfTurns()];
		for (int i = 0; i < weightArray.length; i++) {
			weightArray[i] = 1;
		}
		combination.setWeights(new Weights(weightArray));
	}

}
