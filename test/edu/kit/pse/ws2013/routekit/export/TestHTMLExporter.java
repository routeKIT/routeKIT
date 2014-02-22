package edu.kit.pse.ws2013.routekit.export;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.SAXException;

import edu.kit.pse.ws2013.routekit.map.TestDummies;
import edu.kit.pse.ws2013.routekit.routecalculation.Route;
import edu.kit.pse.ws2013.routekit.routecalculation.RouteDescription;
import edu.kit.pse.ws2013.routekit.routecalculation.TurnInstruction;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;

public class TestHTMLExporter {
	HTMLExporter exporter;
	RouteDescription description;
	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	@Before
	public void setUp() {
		exporter = new HTMLExporter();
		List<TurnInstruction> instructions = Arrays
				.asList(new TurnInstruction[] { new TurnInstruction(0, "Foo"),
						new TurnInstruction(1, "Bar") });
		TestDummies dummies = new TestDummies();
		description = new RouteDescription(new Route(
				dummies.getProfileMapCombination(), new PointOnEdge(0, .5f),
				new PointOnEdge(1, .75f), null), instructions);
	}

	@Test
	public void testValidXml() throws IOException, SAXException,
			ParserConfigurationException {
		File file = tmpFolder.newFile();
		exporter.exportRouteDescription(description, file);
		DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
	}
}
