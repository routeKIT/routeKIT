package test.edu.kit.pse.ws2013.routekit.map;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import edu.kit.pse.ws2013.routekit.map.EdgeBasedGraph;
import edu.kit.pse.ws2013.routekit.map.MultipleRestrictions;
import edu.kit.pse.ws2013.routekit.map.Restriction;
import edu.kit.pse.ws2013.routekit.map.TurnType;
import edu.kit.pse.ws2013.routekit.map.VehicleTypeRestriction;
import edu.kit.pse.ws2013.routekit.map.WeightRestriction;
import edu.kit.pse.ws2013.routekit.profiles.VehicleType;

public class TestEdgeBasedGraph {

	@Test
	public void testSaveLoad() throws IOException {
		int[] edges = new int[] { 0, 3, 6, 6 };
		int[] turns = new int[] { 1, 2, 3, 0, 2, 3, 0, 1, 2 };
		TurnType[] turnTypes = new TurnType[] { TurnType.HalfLeftTurn,
				TurnType.HalfRightTurn, TurnType.MotorwayJunction,
				TurnType.RightTurn, TurnType.NoTurn, TurnType.StraightOn,
				TurnType.RoundaboutNoExit, TurnType.RoundaboutEntry,
				TurnType.HalfLeftTurn };
		int[] partitions = new int[] { 0, 1, 2, 3 };
		Map<Integer, Restriction> restrictions = new HashMap<>();
		restrictions.put(0, WeightRestriction.getInstance(100));
		restrictions.put(2, MultipleRestrictions.getInstance(Arrays.asList(
				VehicleTypeRestriction.getInstance(VehicleType.Truck),
				VehicleTypeRestriction.getInstance(VehicleType.Bus))));
		EdgeBasedGraph e = new EdgeBasedGraph(edges, turns, turnTypes,
				restrictions);
		e.setPartitions(partitions);
		File f = File.createTempFile("routeKit_testEdgeBasedGraph_", ".graph");
		e.save(f);
		EdgeBasedGraph loaded = EdgeBasedGraph.load(f);
		assertEquals(turns.length, loaded.getNumberOfTurns());
		for (int edge = 0; edge < edges.length; edge++) {
			// assertEquals(e.getIncomingTurns(edge), // TODO unimplemented
			// loaded.getIncomingTurns(edge));
			// assertEquals(e.getOutgoingTurns(edge),
			// loaded.getOutgoingTurns(edge));
			assertEquals(e.getPartition(edge), loaded.getPartition(edge));
		}
		for (int turn = 0; turn < turns.length; turn++) {
			// assertEquals(e.getStartEdge(turn), loaded.getStartEdge(turn));
			// assertEquals(e.getTargetEdge(turn), loaded.getTargetEdge(turn));
			assertEquals(e.getTurnType(turn), loaded.getTurnType(turn));
		}
	}
}
