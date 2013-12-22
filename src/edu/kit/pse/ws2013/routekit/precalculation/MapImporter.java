package edu.kit.pse.ws2013.routekit.precalculation;
import java.io.File;

import edu.kit.pse.ws2013.routekit.map.StreetMap;
/**
 * Stellt die Funktionalität zum Importieren einer neuen Karte bereit.
 */
public class MapImporter {
	/**
	 * Importiert eine neue Karte aus der angegebenen OpenStreetMap-Datei. Die
	 * vom {@link OSMParser} aufgebaute Graphdatenstruktur wird dabei vom
	 * {@link GraphPartitioner} partitioniert und zurückgegeben.
	 * 
	 * @param file
	 *            Die OpenStreetMap-Datei, aus der die Kartendaten importiert
	 *            werden sollen.
	 * @param name
	 *            Der Name der neuen {@link StreetMap}.
	 * @return
	 */
	public StreetMap importMap(File file, String name) {
		return null;
	}
}
