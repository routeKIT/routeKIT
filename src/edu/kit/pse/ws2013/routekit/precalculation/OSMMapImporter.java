package edu.kit.pse.ws2013.routekit.precalculation;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

import edu.kit.pse.ws2013.routekit.map.StreetMap;

public class OSMMapImporter extends MapImporter {
	OSMParser parser = new OSMParser();
	GraphPartitioner part = new DummyGraphPartitioner();

	@Override
	public StreetMap importMap(File file, String name) throws IOException,
			SAXException {
		StreetMap stm = parser.parseOSM(file);
		stm.setName(name);
		part.partitionGraph(stm.getEdgeBasedGraph(), 1);
		return stm;
	}

}
