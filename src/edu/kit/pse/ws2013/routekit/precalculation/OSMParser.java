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
import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.util.Coordinates;

/**
 * This class provides the functionality to parse an OpenStreetMap data file.
 */
public class OSMParser {
	/*
	 * Maps OSM node IDs to our node indexes.
	 */
	private Map<Long, Integer> nodes = new HashMap<>();

	/*
	 * Contains the outgoing edges for each node.
	 */
	private List<Set<MapEdge>> edges = new ArrayList<>();

	private int numberOfEdges = 0;

	private float lat[];
	private float lon[];

	private EdgeProperties edgeProps[];
	private int nodeArray[];
	private int edgeArray[];

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
	public StreetMap parseOSM(File file) throws SAXException, IOException {
		if (file == null) {
			throw new IllegalArgumentException();
		}

		SAXParser parser = null;
		try {
			parser = SAXParserFactory.newInstance().newSAXParser();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		parser.parse(file, new FirstRunHandler());
		parser.parse(file, new SecondRunHandler());

		createAdjacencyField();
		Graph graph = new Graph(nodeArray, edgeArray, null, edgeProps, lat, lon);

		return new StreetMap(graph, null);
	}

	private void createAdjacencyField() {
		nodeArray = new int[nodes.size()];
		edgeArray = new int[numberOfEdges];
		edgeProps = new EdgeProperties[numberOfEdges];

		int edgeCount = 0;
		for (int node = 0; node < nodes.size(); node++) {
			nodeArray[node] = edgeCount;
			for (MapEdge edge : edges.get(node)) {
				edgeProps[edgeCount] = edge.getWay().getEdgeProperties();
				edgeArray[edgeCount] = edge.getTargetNode();
				edgeCount++;
			}
		}
	}

	/*
	 * First run: Get edges from OSM ways
	 */
	private class FirstRunHandler extends DefaultHandler {
		private String enclosing;

		private Map<String, String> wayTags;
		private List<Long> wayNodes;

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attr) throws SAXException {
			switch (qName) {
			case "way":
				wayTags = new HashMap<>();
				wayNodes = new ArrayList<>();
				// fall through
			case "osm":
				enclosing = qName;
				break;
			case "nd":
				wayNodes.add(Long.parseLong(attr.getValue("ref")));
				break;
			case "tag":
				if (enclosing.equals("way")) {
					wayTags.put(attr.getValue("k"), attr.getValue("v"));
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) {
			if (qName.equals("way")) {
				OSMWay way = new OSMWay(wayTags);
				if (way.getHighwayType() != null) {
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
				int node = nodes.get(Integer.parseInt(attr.getValue("id")));
				try {
					lat[node] = Coordinates.parseLatitude(attr.getValue("lat"));
					lon[node] = Coordinates
							.parseLongitude(attr.getValue("lon"));
				} catch (IllegalArgumentException e) {
					throw new SAXParseException(null, locator, e);
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
