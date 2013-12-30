package edu.kit.pse.ws2013.routekit.map;

import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * Abstrakte Klasse zur Repräsentation unterschiedlicher Beschränkungen für
 * Straßen oder Abbiegemöglichkeiten. Die einzelnen Unterklassen sind Multitons,
 * um nicht unnötig Speicherplatz für mehrere gleiche Objekte zu verbrauchen.
 */
public class Restriction {
	/**
	 * Bestimmt, ob es die Beschränkung erlaubt, unter dem angegebenen Profil
	 * eine Straße oder Abbiegemöglichkeit zu nutzen.
	 * 
	 * @param profile
	 *            Das verwendete Profil.
	 * @return
	 */
	public boolean allows(Profile profile) {
		return false;
	}
}
