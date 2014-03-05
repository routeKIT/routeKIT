package edu.kit.pse.ws2013.routekit.map;

import static edu.kit.pse.ws2013.routekit.map.TurnType.*;

import java.util.Arrays;
import java.util.HashMap;

import edu.kit.pse.ws2013.routekit.models.ArcFlags;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.Weights;
import edu.kit.pse.ws2013.routekit.profiles.Profile;

public class TestDummies {
	private final int[] nodes;
	private final int[] edges;
	private final int[] eEdges;
	private final int[] eTurns;
	private final TurnType[] eTurnTypes;
	private final int[] partitions;
	private final ArcFlags arcFlags;
	private final Weights weights;
	private final Graph graph;
	private final EdgeBasedGraph eGraph;
	private final StreetMap map;
	private final ProfileMapCombination pmc;

	// 0 1
	// 2 3
	public TestDummies() {
		nodes = new int[] { 0, 3, 6, 6 };
		edges = new int[] { 1, 2, 3, 0, 2, 3, 0, 1, 2 };
		eEdges = new int[] { 0, 2, 2, 4, 6, 6, 8, 10, 12 };
		eTurns = new int[] { 4, 5, 7, 8, 1, 2, 6, 8, 0, 1, 3, 4 };
		eTurnTypes = new TurnType[] { RightTurn, RightTurn, LeftTurn,
				RightTurn, LeftTurn, LeftTurn, RightTurn, RightTurn, RightTurn,
				LeftTurn, LeftTurn, LeftTurn };
		partitions = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		int[] arcFlags = new int[eTurns.length]; // TODO
		Arrays.fill(arcFlags, -1); // TODO
		this.arcFlags = new ArcFlags(arcFlags);
		int[] weights = new int[eTurns.length]; // TODO
		Arrays.fill(weights, 10); // TODO
		this.weights = new Weights(weights);
		HashMap<Integer, NodeProperties> nodeProps = new HashMap<>();
		EdgeProperties[] edgeProps = new EdgeProperties[edges.length];
		Arrays.fill(edgeProps, new EdgeProperties(HighwayType.Tertiary,
				"Bogus Str.", null, 30));
		float[] lat = new float[] { 1, 1, 0, 0 };
		float[] lon = new float[] { 0, 1, 0, 1 };
		graph = new Graph(nodes, edges, nodeProps, edgeProps, lat, lon);
		eGraph = new EdgeBasedGraph(eEdges, eTurns, eTurnTypes,
				new HashMap<Integer, Restriction>());
		eGraph.setPartitions(partitions);
		map = new StreetMap(graph, eGraph);
		map.setName("Dummy map for testing");
		pmc = new ProfileMapCombination(map, Profile.defaultCar, this.weights,
				this.arcFlags, 10);
	}

	public Graph getGraph() {
		return graph;
	}

	public EdgeBasedGraph getEdgeBasedGraph() {
		return eGraph;
	}

	public StreetMap getMap() {
		return map;
	}

	public ProfileMapCombination getProfileMapCombination() {
		return pmc;
	}
}
