package edu.kit.pse.ws2013.routekit.precalculation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.kit.pse.ws2013.routekit.map.EdgeBasedGraph;
import edu.kit.pse.ws2013.routekit.map.Graph;
import edu.kit.pse.ws2013.routekit.map.StreetMap;

/**
 * This class provides the functionality to parse an OpenStreetMap data file.
 */
public class OSMParser {
	/*
	 * Maps OSM node IDs to our node indexes.
	 */
	private Map<Integer, Integer> nodes = new HashMap<>();

	/*
	 * Contains the outgoing edges for each node.
	 */
	private List<Set<MapEdge>> edges = new ArrayList<>();

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
	 */
	public StreetMap parseOSM(File file) throws SAXException, IOException {
		if (file == null) {
			throw new IllegalArgumentException();
		}

		try {
			SAXParserFactory.newInstance().newSAXParser()
					.parse(file, new FirstRunHandler());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

	private abstract class Handler extends DefaultHandler {
		protected Locator loc;

		@Override
		public void setDocumentLocator(Locator loc) {
			this.loc = loc;
		}
	}

	private class FirstRunHandler extends Handler {
		private String enclosing;

		private Map<String, String> tags;
		private List<Integer> nodes;

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attr) throws SAXException {
			switch (qName) {
			case "way":
				tags = new HashMap<>();
				nodes = new ArrayList<>();
			case "osm":
				enclosing = qName;
				return;
			case "nd":
				nodes.add(Integer.parseInt(attr.getValue("ref")));
			case "tag":
				if (enclosing.equals("way")) {
					tags.put(attr.getValue("k"), attr.getValue("v"));
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) {
			if (qName.equals("way")) {
				OSMWay way = new OSMWay(tags);

				// These lists have done their job, throw them away
				tags = null;
				nodes = null;
			}
			if (qName.equals(enclosing)) {
				enclosing = qName.equals("osm") ? null : "osm";
			}
		}
	}
}
