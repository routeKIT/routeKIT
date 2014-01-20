package edu.kit.pse.ws2013.routekit.precalculation;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

import edu.kit.pse.ws2013.routekit.map.StreetMap;

/**
 * This interface provides the functionality to import a new map.
 */
public abstract class MapImporter {
	public MapImporter() {
	}

	/**
	 * Imports a new map from the specified OpenStreetMap data file. The graph
	 * data structure built by the {@link OSMParser} is partitioned using a
	 * {@link GraphPartitioner} and then returned.
	 * 
	 * @param file
	 *            the OSM file from which the map data is to be imported
	 * @param name
	 *            the name of the new {@link StreetMap}
	 * @return the imported {@link StreetMap}
	 * @throws IllegalArgumentException
	 *             if {@code file} is {@code null}
	 * @throws IOException
	 *             if any I/O error occurs
	 * @throws SAXException
	 *             if an error occurs during parsing the OSM file, e.g. if the
	 *             file format is invalid
	 */
	public abstract StreetMap importMap(File file, String name)
			throws IOException, SAXException;

}
