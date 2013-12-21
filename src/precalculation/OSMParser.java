package precalculation;
import java.io.File;

import map.EdgeBasedGraph;
import map.Graph;
import map.StreetMap;
/**
 * Stellt die Funktionalität zum Parsen einer OpenStreetMap-Datei bereit. Dafür
 * werden zur temporären Repräsentation des Graphen im Speicher die Klassen
 * {@link OSMWay}, {@link MapEdge} und {@link TurnRestriction} verwendet.
 */
public class OSMParser {
	/**
	 * Liest eine OpenStreetMap-Datei ein und erzeugt daraus einen {@link Graph}
	 * sowie den zugehörigen (unpartitionierten) {@link EdgeBasedGraph} und gibt
	 * diese als {@link StreetMap} zurück.
	 * 
	 * @param file
	 *            Die OpenStreetMap-Datei, die eingelesen werden soll.
	 * @param name
	 *            Der Name der neuen {@link StreetMap}.
	 * @return
	 */
	public StreetMap parseOSM(File file, String name) {
		return null;
	}
}
