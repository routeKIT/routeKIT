package edu.kit.pse.ws2013.routekit.precalculation;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

import edu.kit.pse.ws2013.routekit.map.StreetMap;

public class OSMMapImporter implements MapImporter {
	OSMParser parser = new OSMParser();

	@Override
	public StreetMap importMap(File file, String name) throws IOException,
			SAXException {
		StreetMap stm = parser.parseOSM(file);
		stm.setName(name);

		return stm;
	}

}
