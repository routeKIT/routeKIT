package history;
import java.io.File;
import java.util.List;

import util.Coordinates;
/**
 * Kapselt den Verlauf.
 */
public class History {
	/**
	 * Speichert den Verlauf in die angegebene Datei.
	 * 
	 * @param file
	 *            Die Datei, in die der Verlauf gespeichert wird.
	 */
	public void save(File file) {
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
		return null;
	}
}
