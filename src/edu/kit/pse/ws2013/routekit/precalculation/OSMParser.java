package edu.kit.pse.ws2013.routekit.precalculation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import edu.kit.pse.ws2013.routekit.map.EdgeBasedGraph;
import edu.kit.pse.ws2013.routekit.map.EdgeProperties;
import edu.kit.pse.ws2013.routekit.map.Graph;
import edu.kit.pse.ws2013.routekit.map.NodeProperties;
import edu.kit.pse.ws2013.routekit.map.Restriction;
import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.map.TurnType;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;
import edu.kit.pse.ws2013.routekit.util.Coordinates;

/**
 * This class provides the functionality to parse an OpenStreetMap data file.
 */
public class OSMParser {
	private Graph graph;
	private EdgeBasedGraph edgeBasedGraph;

	/*
	 * Maps OSM node IDs to our node indexes.
	 */
	private Map<Long, Integer> nodes = new HashMap<>();

	/*
	 * Contains the outgoing edges for each node.
	 */
	private List<Set<MapEdge>> edges = new ArrayList<>();

	private int numberOfEdges = 0;
	private int numberOfTurns = 0;

	private float lat[];
	private float lon[];

	/*
	 * The arrays for creating the Graph.
	 */
	private int graphNodes[];
	private int graphEdges[];
	private EdgeProperties edgeProps[];

	/*
	 * The arrays for creating the EdgedBasedGraph.
	 */
	private int ebgEdges[];
	private int ebgTurns[];
	private TurnType turnTypes[];

	/**
	 * Reads an OpenStreetMap data file and creates a {@link Graph} from it as
	 * well as the corresponding (not yet partitioned) {@link EdgeBasedGraph}
	 * and returns them as a {@link StreetMap} object.
	 * 
	 * @param file
	 *            the OSM file to be read
	 * @return the {@link StreetMap} created from the OSM file
	 * @throws IllegalArgumentException
	 *             if {@code file} is {@code null}
	 * @throws IOException
	 *             if any I/O error occurs
	 * @throws SAXException
	 *             TODO
	 */
	public StreetMap parseOSM(File file, ProgressReporter reporter)
			throws SAXException, IOException {
		if (file == null) {
			throw new IllegalArgumentException();
		}

		reporter.setSubTasks(new float[] { .2f, .2f, .3f, .3f });

		SAXParser parser = null;
		try {
			parser = SAXParserFactory.newInstance().newSAXParser();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		reporter.pushTask("Erster Durchlauf");
		parser.parse(file, new FirstRunHandler());
		reporter.popTask();
		reporter.pushTask("Zweiter Durchlauf");
		parser.parse(file, new SecondRunHandler());
		reporter.popTask();

		reporter.pushTask("Baue Adjazenzfelder auf");
		createAdjacencyField();
		reporter.popTask();
		graph = new Graph(graphNodes, graphEdges,
				new HashMap<Integer, NodeProperties>(), edgeProps, lat, lon);

		reporter.pushTask("Baue kantenbasierten Graph auf");
		buildEdgeBasedGraph();
		reporter.popTask();
		edgeBasedGraph = new EdgeBasedGraph(ebgEdges, ebgTurns, turnTypes,
				new HashMap<Integer, Restriction>());

		return new StreetMap(graph, edgeBasedGraph);
	}

	private void createAdjacencyField() {
		graphNodes = new int[nodes.size()];
		graphEdges = new int[numberOfEdges];
		edgeProps = new EdgeProperties[numberOfEdges];

		int edgeCount = 0;
		for (int node = 0; node < nodes.size(); node++) {
			graphNodes[node] = edgeCount;
			for (MapEdge edge : edges.get(node)) {
				edge.setId(edgeCount);
				graphEdges[edgeCount] = edge.getTargetNode();
				edgeProps[edgeCount] = edge.getWay().getEdgeProperties();
				edgeCount++;
				countTurns(node, edge);
			}
		}
	}

	private void countTurns(int node, MapEdge edge) {
		numberOfTurns += edges.get(edge.getTargetNode()).size();
		for (MapEdge nextEdge : edges.get(edge.getTargetNode())) {
			if (nextEdge.getTargetNode() == node) {
				// the way back is not a valid turn
				numberOfTurns--;
			}
		}
	}

	private void buildEdgeBasedGraph() {
		ebgEdges = new int[numberOfEdges];
		ebgTurns = new int[numberOfTurns];
		turnTypes = new TurnType[numberOfTurns];

		int edgeCount = 0;
		int turnCount = 0;
		for (int node = 0; node < edges.size(); node++) {
			for (MapEdge fromEdge : edges.get(node)) {
				ebgEdges[edgeCount] = turnCount;
				for (MapEdge toEdge : edges.get(fromEdge.getTargetNode())) {
					if (toEdge.getTargetNode() != node) {
						ebgTurns[turnCount] = toEdge.getId();
						turnTypes[turnCount] = determineTurnType(node,
								fromEdge, toEdge);
						turnCount++;
					}
				}
				edgeCount++;
			}
		}
	}

	private TurnType determineTurnType(int startNode, MapEdge fromEdge,
			MapEdge toEdge) {
		int turnNode = fromEdge.getTargetNode();
		if (graph.getOutgoingEdges(turnNode).size() == 1) {
			return TurnType.NoTurn;
		}
		// TODO: missing turn types
		float angle = graph.getCoordinates(turnNode).angleBetween(
				graph.getCoordinates(startNode),
				graph.getCoordinates(toEdge.getTargetNode()));
		if (angle <= 130) {
			return TurnType.RightTurn;
		}
		if (angle <= 160) {
			return TurnType.HalfRightTurn;
		}
		if (angle <= 200) {
			return TurnType.StraightOn;
		}
		if (angle <= 240) {
			return TurnType.HalfLeftTurn;
		}
		return TurnType.LeftTurn;
	}

	/*
	 * First run: Get edges from OSM ways
	 */
	private class FirstRunHandler extends DefaultHandler {
		private String enclosing;
		private Locator locator;

		private int wayId;
		private Map<String, String> wayTags;
		private List<Long> wayNodes;

		@Override
		public void setDocumentLocator(Locator locator) {
			this.locator = locator;
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attr) throws SAXException {
			switch (qName) {
			case "way":
				try {
					wayId = Integer.parseInt(attr.getValue("id"));
				} catch (NumberFormatException e) {
					throw new SAXParseException("Way ID missing or invalid",
							locator, e);
				}
				wayTags = new HashMap<>();
				wayNodes = new ArrayList<>();
				// fall through
			case "osm":
				enclosing = qName;
				break;
			case "nd":
				try {
					wayNodes.add(Long.parseLong(attr.getValue("ref")));
				} catch (NumberFormatException e) {
					throw new SAXParseException("Way " + wayId
							+ ": Invalid or missing node ID reference",
							locator, e);
				}
				break;
			case "tag":
				if (enclosing.equals("way")) {
					String key = attr.getValue("k");
					String value = attr.getValue("v");
					if (key == null || value == null) {
						throw new SAXParseException("Way " + wayId
								+ ": Invalid tag (key or value missing)",
								locator);
					}
					wayTags.put(key, value);
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) {
			if (qName.equals("way")) {
				OSMWay way = new OSMWay(wayTags);
				if (way.getHighwayType() != null) {
					way.setId(wayId);
					for (int i = 0; i < wayNodes.size() - 1; i++) {
						int from = resolveNodeID(wayNodes.get(i));
						int to = resolveNodeID(wayNodes.get(i + 1));
						if (!way.isReversedOneway()) {
							addEdge(from, to, way);
						}
						if (!way.isOneway()) {
							addEdge(to, from, way);
						}
					}
				}

				// These lists have done their job now, throw them away
				wayTags = null;
				wayNodes = null;
			}
			if (qName.equals(enclosing)) {
				enclosing = qName.equals("osm") ? null : "osm";
			}
		}

		private void addEdge(int from, int to, OSMWay way) {
			edges.get(from).add(new MapEdge(to, way));
			numberOfEdges++;
		}

		private int resolveNodeID(Long id) {
			if (!nodes.containsKey(id)) {
				nodes.put(id, nodes.size());
				edges.add(new HashSet<MapEdge>(1));
			}
			return nodes.get(id);
		}
	}

	/*
	 * Second run: Get coordinates and properties from nodes, add turn
	 * restrictions
	 */
	private class SecondRunHandler extends DefaultHandler {
		private String enclosing;
		private Locator locator;

		private long nodeId;

		@Override
		public void setDocumentLocator(Locator locator) {
			this.locator = locator;
		}

		@Override
		public void startDocument() {
			lat = new float[nodes.size()];
			lon = new float[nodes.size()];
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attr) throws SAXException {
			switch (qName) {
			case "node":
				try {
					nodeId = Long.parseLong(attr.getValue("id"));
				} catch (NumberFormatException e) {
					throw new SAXParseException("Node ID missing or invalid",
							locator, e);
				}
				if (nodes.containsKey(nodeId)) {
					int node = nodes.get(nodeId);
					try {
						lat[node] = Coordinates.parseLatitude(attr
								.getValue("lat"));
						lon[node] = Coordinates.parseLongitude(attr
								.getValue("lon"));
					} catch (IllegalArgumentException e) {
						throw new SAXParseException("Node " + nodeId
								+ ": Coordinates invalid or unspecified",
								locator, e);
					}
				}
				// fall through
			case "osm":
				enclosing = qName;
				break;
			case "tag":
				if (enclosing.equals("node")) {
					// TODO: create NodeProperties
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) {
			if (qName.equals(enclosing)) {
				enclosing = qName.equals("osm") ? null : "osm";
			}
		}
	}
}
