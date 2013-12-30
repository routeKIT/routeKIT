package edu.kit.pse.ws2013.routekit.controllers;

import java.util.Set;

import edu.kit.pse.ws2013.routekit.map.StreetMap;

/**
 * Verwaltet die Kartendaten. Hat intern eine Menge von vorhandenen Kartendaten.
 */
public class MapManager {
	/**
	 * Löscht die ausgewählte Karte aus der internen Liste und von der
	 * Festplatte (die zwei Graphen, Daten über Beschränkungen und alle
	 * Vorberechnung).
	 * 
	 * @param map
	 *            Die Karte, die gelöscht werden soll.
	 */
	public void deleteMap(StreetMap map) {
	}

	/**
	 * Gibt alle Karte in der internen Liste zurück.
	 * 
	 * @return
	 */
	public Set<StreetMap> getMaps() {
		return null;
	}

	/**
	 * Speichert die ausgewählte Karte in der internen Liste und auf der
	 * Festplatte.
	 * 
	 * (Der Speicherort wird vom Manager deckend verwaltet.)
	 * 
	 * @param map
	 *            Die Karte, die gespeichert werden soll.
	 */
	public void saveMap(StreetMap map) {
	}
}
