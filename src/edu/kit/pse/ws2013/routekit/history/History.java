package edu.kit.pse.ws2013.routekit.history;
import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.kit.pse.ws2013.routekit.util.Coordinates;
/**
 * Kapselt den Verlauf.
 */
public class History {
	
	private final List<HistoryEntry> entries;
	
	/**
	 * Create a new, empty {link History}.
	 */
	public History() {
		this.entries = new LinkedList<>();
	}
	
	/**
	 * <p>
	 * Create a new {@link History} with the specified entries.
	 * </p><p>
	 * For internal use only!
	 * </p>
	 * @param entries The entries of the new {@link History}.
	 * @see #load(File)
	 */
	private History(List<HistoryEntry> entries) {
		this.entries = entries;
	}

	/**
	 * Fügt einen Eintrag zum Verlauf hinzu. Als {@code date} des neuen Eintrags
	 * wird die aktuelle Zeit verwendet.
	 * 
	 * @param start
	 *            Der Startpunkt.
	 * @param destination
	 *            Der Zielpunkt.
	 */
	public void addEntry(Coordinates start, Coordinates destination) {
		entries.add(new HistoryEntry(start, destination, new Date()));
	}
	
	/**
	 * Speichert den Verlauf in die angegebene Datei.
	 * 
	 * @param file
	 *            Die Datei, in die der Verlauf gespeichert wird.
	 */
	public void save(File file) {
	}
	/**
	 * (statisch) Lädt einen Verlauf aus der angegebenen Datei und gibt ihn
	 * zurück.
	 * 
	 * @param file
	 *            Die Datei, aus der der Verlauf geladen wird.
	 * @return
	 */
	public History load(File file) {
		return null;
	}
	
	/**
	 * Gibt alle Einträge des Verlaufs zurück.
	 * 
	 * @return
	 */
	public List<HistoryEntry> getEntries() {
		return Collections.unmodifiableList(entries);
	}
}
