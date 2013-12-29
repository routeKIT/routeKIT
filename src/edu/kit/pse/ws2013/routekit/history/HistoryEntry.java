package edu.kit.pse.ws2013.routekit.history;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.kit.pse.ws2013.routekit.util.Coordinates;
/**
 * A {@link History} entry, consisting of
 * <ul>
 * <li>a {@link #getStart() start point}</li>
 * <li>a {@link #getDest() destination point}</li>
 * <li>a {@link #getDate() date}</li>
 * </ul>
 */
public class HistoryEntry {
	
	private static final DateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
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
	
	/**
	 * <p>
	 * Returns a string representation of the history entry
	 * suitable for parsing with {@link #fromString(String)}.
	 * </p><p>
	 * The exact format is:
	 * {@code date: start -> dest},
	 * where {@code date} is the {@link #getDate() date} in ISO 8601 format, e.&nbsp;g. {@code 2013-12-27T17:11:00+0100},
	 * {@code start} is the {@link #getStart() start} and {@code dest} is the {@link #getDest() destination} point.
	 * </p>
	 * @return A string representation of the history entry.
	 */
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append(ISO_FORMAT.format(date));
		ret.append(": ");
		ret.append(start.toString());
		ret.append(" -> ");
		ret.append(dest.toString());
		return ret.toString();
	}
	
	/**
	 * Parse a history entry string as returned by {@link #toString()}
	 * back into a {@link HistoryEntry}.
	 * @param s The history entry string.
	 * @return The {@link HistoryEntry} parsed from the string.
	 * @throws IllegalArgumentException If the history entry string can’t be parsed.
	 */
	public static HistoryEntry fromString(String s) {
		if(s == null) {
			throw new IllegalArgumentException("History entry string is null!", new NullPointerException());
		}
		try {
			Matcher m = Pattern.compile("(.*): (.*) -> (.*)").matcher(s);
			if(!m.matches()) { // matches() is necessary because the matcher is lazy
				throw new IllegalArgumentException("No match found!");
			}
			Date date = ISO_FORMAT.parse(m.group(1));
			Coordinates start = Coordinates.fromString(m.group(2));
			Coordinates dest = Coordinates.fromString(m.group(3));
			return new HistoryEntry(start, dest, date);
		} catch (ParseException|IllegalArgumentException e) {
			throw new IllegalArgumentException("Can’t parse history entry string!", e);
		}
	}
}
