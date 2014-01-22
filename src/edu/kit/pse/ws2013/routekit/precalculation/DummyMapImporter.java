package edu.kit.pse.ws2013.routekit.precalculation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.xml.sax.SAXException;

import edu.kit.pse.ws2013.routekit.map.EdgeBasedGraph;
import edu.kit.pse.ws2013.routekit.map.EdgeProperties;
import edu.kit.pse.ws2013.routekit.map.Graph;
import edu.kit.pse.ws2013.routekit.map.HighwayType;
import edu.kit.pse.ws2013.routekit.map.NodeProperties;
import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.map.TurnType;
import edu.kit.pse.ws2013.routekit.map.Restriction;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;

/**
 * This class import dummy data.
 */
public class DummyMapImporter extends MapImporter {
	@Override
	public StreetMap importMap(File file, String name, ProgressReporter reporter)
			throws IOException, SAXException {
		if (file == null) {
			throw new IllegalArgumentException();
		}

		// TODO: dummy implementation
		int nodes[] = { 0, 2, 4, 6 };
		int edges[] = { 1, 2, 0, 3, 0, 3, 1 };

		float lat[] = { 49.002168F, 49.002238F, 49.001506F, 49.001562F };
		float lon[] = { 8.405803F, 8.407928F, 8.405761F, 8.407928F };

		EdgeProperties theProp = new EdgeProperties(HighwayType.Primary,
				"Ringstraße", "B 42", 0);
		EdgeProperties props[] = new EdgeProperties[edges.length];
		Arrays.fill(props, theProp);

		Graph graph = new Graph(nodes, edges,
				new HashMap<Integer, NodeProperties>(), props, lat, lon);

		EdgeBasedGraph edgeGraph = createDummyEdgedBasedGraph();
		int partitions[] = { 1, 1, 1, 1, 1, 1, 1 };
		edgeGraph.setPartitions(partitions);

		StreetMap map = new StreetMap(graph, edgeGraph);
		map.setName(name);
		return map;
	}

	private EdgeBasedGraph createDummyEdgedBasedGraph() {
		int edges[] = { 0, 1, 2, 3, 3, 5, 6 };
		int turns[] = { 3, 5, 1, 0, 6, 2 };
		TurnType types[] = { TurnType.RightTurn, TurnType.LeftTurn,
				TurnType.LeftTurn, TurnType.RightTurn, TurnType.LeftTurn,
				TurnType.LeftTurn };
		return new EdgeBasedGraph(edges, turns, types,
				new HashMap<Integer, Restriction>());
	}
}
