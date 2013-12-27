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
	 * @throws IllegalArgumentException If start, dest or date are null.
	 */
	public HistoryEntry(Coordinates start, Coordinates dest, Date date) {
		if(start == null) {
			throw new IllegalArgumentException("start is null!", new NullPointerException());
		}
		if(dest == null) {
			throw new IllegalArgumentException("dest is null!", new NullPointerException());
		}
		if(date == null) {
			throw new IllegalArgumentException("date is null!", new NullPointerException());
		}
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
