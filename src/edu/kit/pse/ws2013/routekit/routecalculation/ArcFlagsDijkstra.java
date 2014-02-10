package edu.kit.pse.ws2013.routekit.routecalculation;

import edu.kit.pse.ws2013.routekit.models.ArcFlags;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;

/**
 * Uses Dijkstra’s Algorithm to determine the fastest route between start and
 * destination point for the current {@link ProfileMapCombination}. The
 * calculation is sped up by usage of Arc-Flags.
 */
public class ArcFlagsDijkstra extends Dijkstra {

	ArcFlags flags;

	@Override
	public Route calculateRoute(PointOnEdge start, PointOnEdge destination,
			ProfileMapCombination data) {
		flags = data.getArcFlags();
		return super.calculateRoute(start, destination, data);
	}

	@Override
	protected boolean allowsTurn(int turn) {
		// fetch Arc-Flags
		final int arcFlag = flags.getFlag(turn);
		int arcBit = (arcFlag >> destinationPartition) & 0x1;

		if (destinationCorrespondingPartition != -1) {
			arcBit |= (arcFlag >> destinationCorrespondingPartition) & 0x1;
		}

		// check arc bit
		return arcBit != 0;
	}
}
