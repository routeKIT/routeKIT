package edu.kit.pse.ws2013.routekit.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import edu.kit.pse.ws2013.routekit.map.Graph;
import edu.kit.pse.ws2013.routekit.routecalculation.Route;
import edu.kit.pse.ws2013.routekit.routecalculation.RouteDescription;
import edu.kit.pse.ws2013.routekit.routecalculation.TurnInstruction;
import edu.kit.pse.ws2013.routekit.util.Coordinates;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;

/**
 * Provides the functionality to export a {@link RouteDescription} into an HTML
 * document.
 */
public class HTMLExporter {

	/**
	 * Exports the given {@link RouteDescription} into an HTML document into the
	 * specified file.
	 * 
	 * @param routeDesc
	 *            the {@link RouteDescription} to be exported
	 * @param file
	 *            the file where the HTML document should be written
	 * @throws IOException
	 *             if any I/O error occurs
	 * @throws IllegalArgumentException
	 *             if {@code routeDesc} or {@code file} is {@code null}
	 */
	public void exportRouteDescription(RouteDescription routeDesc, File file)
			throws IOException {
		if (routeDesc == null || file == null) {
			throw new IllegalArgumentException();
		}

		try (OutputStream outputStream = new FileOutputStream(file);
				Writer writer = new OutputStreamWriter(outputStream)) {
			XMLStreamWriter html = XMLOutputFactory.newInstance()
					.createXMLStreamWriter(outputStream, "utf-8");
			html.writeStartDocument();
			writer.write("<!DOCTYPE html>");
			html.writeStartElement("html");

			html.writeStartElement("head");

			html.writeEmptyElement("meta");
			html.writeAttribute("charset", "utf-8");

			// TODO stylesheet here

			html.writeEndElement(); // head

			html.writeStartElement("body");

			html.writeStartElement("h1");
			html.writeCharacters("routeKIT");
			html.writeEndElement();

			html.writeStartElement("h2");
			Route route = routeDesc.getRoute();
			Graph graph = route.getData().getStreetMap().getGraph();
			PointOnEdge startPoint = route.getStart();
			int startEdge = startPoint.getEdge();
			Coordinates start = graph.getCoordinates(
					graph.getStartNode(startEdge)).goIntoDirection(
					graph.getCoordinates(graph.getTargetNode(startEdge)),
					startPoint.getPosition());
			PointOnEdge destPoint = route.getDestination();
			int destEdge = destPoint.getEdge();
			Coordinates dest = graph.getCoordinates(
					graph.getStartNode(destEdge)).goIntoDirection(
					graph.getCoordinates(graph.getTargetNode(destEdge)),
					destPoint.getPosition());
			html.writeCharacters("Route von " + start + " nach " + dest);
			html.writeEndElement();

			html.writeStartElement("ol");
			for (TurnInstruction instruction : routeDesc.getInstructions()) {
				html.writeStartElement("li");
				html.writeCharacters(instruction.toString());
				html.writeEndElement();
			}
			html.writeEndElement();

			html.writeEndElement(); // body

			html.writeEndElement(); // html
			html.writeEndDocument();

			html.close();
		} catch (XMLStreamException e) {
			// TODO
			e.printStackTrace();
		}
	}
}
