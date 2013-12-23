package edu.kit.pse.ws2013.routekit.history;
import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.kit.pse.ws2013.routekit.util.Coordinates;
/**
 * A history of route calculation queries.
 */
public class History {
	
	/**
	 * The internal list of history entries.
	 */
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
	 * Add a new {@link HistoryEntry entry} to the {@link History}.
	 * The {@link HistoryEntry#getDate() date} of the new entry will be the current date.
	 * 
	 * @param start
	 *            The start point.
	 * @param destination
	 *            The destination point.
	 */
	public void addEntry(Coordinates start, Coordinates destination) {
		entries.add(new HistoryEntry(start, destination, new Date()));
	}
	
	/**
	 * Saves the history to the specified file.
	 * 
	 * @param file
	 *            The file where the history should be saved.
	 */
	public void save(File file) {
	}
	/**
	 * Loads a history from the specified file and returns it.
	 * 
	 * @param file
	 *            The file where the history should be loaded.
	 * @return A new {@link History} with the entries from the specified file.
	 */
	public History load(File file) {
		return null;
	}
	
	/**
	 * <p>
	 * Returns all entries from the history.
	 * </p><p>
	 * The history may not be modified through the {@link List}
	 * returned by this method (or its iterator); attempts to do so
	 * will result in an {@link UnsupportedOperationException}
	 * (see {@link Collections#unmodifiableList(List)}).
	 * </p><p>
	 * (Note that while the current implementation will return a list
	 * that will reflect future changes to the history, users should
	 * not rely on this behavior, and instead call this method again
	 * each time the history entries are needed.)
	 * </p>
	 * 
	 * @return An (unmodifiable) {@link List} containing all
	 *         entries of the history.
	 */
	public List<HistoryEntry> getEntries() {
		return Collections.unmodifiableList(entries);
	}
}
