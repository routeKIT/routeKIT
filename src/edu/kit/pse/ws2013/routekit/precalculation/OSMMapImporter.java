package edu.kit.pse.ws2013.routekit.precalculation;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;

public class OSMMapImporter extends MapImporter {
	OSMParser parser = new OSMParser();
	GraphPartitioner part = new ExternalPartitionerAdapter();

	@Override
	public StreetMap importMap(File file, String name, ProgressReporter reporter)
			throws IOException, SAXException {
		reporter.setSubTasks(new float[] { .5f, .5f }); // TODO review factors
		reporter.pushTask("Lese OSM-Datei");
		StreetMap stm = parser.parseOSM(file, reporter);
		reporter.popTask();
		stm.setName(name);
		reporter.pushTask("Partitioniere Graph");
		part.partitionGraph(stm.getEdgeBasedGraph(), 32);
		reporter.popTask();
		return stm;
	}

}
