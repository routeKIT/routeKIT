package edu.kit.pse.ws2013.routekit.precalculation;

import java.util.Map;

import edu.kit.pse.ws2013.routekit.map.EdgeProperties;
import edu.kit.pse.ws2013.routekit.map.Restriction;

/**
 * Stellt einen Weg aus der OSM-Datei dar. Dies ist nur eine vom
 * {@link OSMParser} verwendete Hilfsklasse.
 */
public class OSMWay {
	/**
	 * Liefert ein {@link Restriction}-Objekt mit der/den Beschränkung(en) des
	 * Wegs oder {@code null}, falls nicht vorhanden.
	 * 
	 * @return
	 */
	public Restriction getRestriction() {
		return null;
	}

	/**
	 * Bestimmt, ob es sich um einen Kreisverkehr handelt.
	 * 
	 * @return
	 */
	public boolean isRoundabout() {
		return false;
	}

	/**
	 * Bestimmt, ob es sich um eine Einbahnstraße handelt.
	 * 
	 * @return
	 */
	public boolean isOneway() {
		return false;
	}

	/**
	 * Liefert ein {@link EdgeProperties}-Objekt mit den Eigenschaften des Wegs.
	 * 
	 * @return
	 */
	public EdgeProperties getEdgeProperties() {
		return null;
	}

	/**
	 * Konstruktor: Erzeugt ein neues Objekt aus den angegebenen OSM-Tags.
	 * 
	 * @param props
	 *            Eine Liste von OSM-Tags.
	 */
	public OSMWay(Map<String, String> props) {
	}
}
