package test.edu.kit.pse.ws2013.routekit.export;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.SAXException;

import test.edu.kit.pse.ws2013.routekit.map.TestDummies;
import edu.kit.pse.ws2013.routekit.export.GPXExporter;
import edu.kit.pse.ws2013.routekit.routecalculation.Route;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;

public class TestGPXExporter {
	GPXExporter exporter;
	Route route;
	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	@Before
	public void setUp() {
		exporter = new GPXExporter();
		TestDummies dummies = new TestDummies();
		route = new Route(dummies.getProfileMapCombination(), new PointOnEdge(
				0, .125f), new PointOnEdge(4, .75f),
				Arrays.asList(new Integer[] { 0 }));
	}

	@Test
	public void testValidXml() throws SAXException, IOException,
			ParserConfigurationException, XMLStreamException {
		File file = tmpFolder.newFile();
		exporter.exportRoute(route, file);
		DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
	}
}
