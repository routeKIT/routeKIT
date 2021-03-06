package edu.kit.pse.ws2013.routekit.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import edu.kit.pse.ws2013.routekit.routecalculation.Route;
import edu.kit.pse.ws2013.routekit.util.Coordinates;

/**
 * Provides the functionality to export a {@link Route} in the GPS Exchange
 * Format (GPX).
 */
public class GPXExporter {
	/**
	 * Exports the route points of the given route in the GPS Exchange Format
	 * (GPX) into the specified file.
	 * 
	 * @param route
	 *            the {@link Route} to be exported
	 * @param file
	 *            the GPX file to be written
	 * @throws XMLStreamException
	 *             if any error occurs while writing into the file
	 * @throws FileNotFoundException
	 *             if {@code file} cannot be created or if {@code file} exists
	 *             but cannot be overwritten
	 * @throws IllegalArgumentException
	 *             if {@code route} or {@code file} is {@code null}
	 */
	public void exportRoute(Route route, File file)
			throws FileNotFoundException, XMLStreamException {
		if (route == null || file == null) {
			throw new IllegalArgumentException();
		}

		XMLStreamWriter gpx = XMLOutputFactory.newInstance()
				.createXMLStreamWriter(new FileOutputStream(file));
		gpx.writeStartDocument();
		gpx.writeStartElement("gpx");
		gpx.writeAttribute("version", "1.1");
		gpx.writeAttribute("creator", "routeKIT");

		gpx.writeStartElement("metadata");
		gpx.writeStartElement("name");
		gpx.writeCharacters(file.getName());
		gpx.writeEndElement();
		gpx.writeStartElement("time");
		DateFormat xmlDate = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		gpx.writeCharacters(xmlDate.format(new Date()));
		gpx.writeEndElement();
		gpx.writeEndElement();

		gpx.writeStartElement("rte");
		for (Coordinates coords : route) {
			gpx.writeEmptyElement("rtept");
			gpx.writeAttribute("lat", Float.toString(coords.getLatitude()));
			gpx.writeAttribute("lon", Float.toString(coords.getLongitude()));
		}
		gpx.writeEndElement();

		gpx.writeEndElement();
		gpx.writeEndDocument();
		gpx.close();
	}
}
