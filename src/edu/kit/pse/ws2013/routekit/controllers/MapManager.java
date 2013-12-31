package edu.kit.pse.ws2013.routekit.controllers;

import java.util.Set;

import edu.kit.pse.ws2013.routekit.map.StreetMap;

/**
 * Verwaltet die Kartendaten. Hat intern eine Menge von vorhandenen Kartendaten.
 */
public class MapManager {

	private static MapManager instance;

	private MapManager() {
		// TODO implement
	}

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

	/**
	 * Initializes the {@link MapManager}.
	 * 
	 * @throws IllegalStateException
	 *             If the MapManager is already initialized.
	 */
	public static void init() {
		if (instance != null) {
			throw new IllegalStateException("Already initialized!");
		}
		instance = new MapManager();
	}

	/**
	 * Returns the {@link MapManager} instance. This is only allowed if the
	 * MapManager was previously {@link #init() initialized}.
	 * 
	 * @return The MapManager instance.
	 * @throws IllegalStateException
	 *             If the MapManager is uninitialized.
	 */
	public static MapManager getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Not yet initialized!");
		}
		return instance;
	}
}
