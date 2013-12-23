package edu.kit.pse.ws2013.routekit.history;
import java.util.Date;

import edu.kit.pse.ws2013.routekit.util.Coordinates;
/**
 * Ein Eintrag im Verlauf.
 */
public class HistoryEntry {
	
	/**
	 * The start point.
	 */
	private final Coordinates start;
	/**
	 * The destination point.
	 */
	private final Coordinates dest;
	/**
	 * The date.
	 */
	private final Date date;
	
	/**
	 * Creates a new {@link HistoryEntry} with the specified attributes.
	 * 
	 * @param start
	 *            The start point.
	 * @param dest
	 *            The destination point.
	 * @param date
	 *            The date.
	 */
	public HistoryEntry(Coordinates start, Coordinates dest, Date date) {
		this.start = start;
		this.dest = dest;
		this.date = date;
	}

	/**
	 * Gets the start point.
	 * @return The start point.
	 */
	public Coordinates getStart() {
		return start;
	}

	/**
	 * Gets the destination point.
	 * @return The destination point.
	 */
	public Coordinates getDest() {
		return dest;
	}

	/**
	 * Gets the date.
	 * @return The date.
	 */
	public Date getDate() {
		return date;
	}
}
