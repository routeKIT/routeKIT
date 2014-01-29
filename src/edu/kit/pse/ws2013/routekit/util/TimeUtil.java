package edu.kit.pse.ws2013.routekit.util;

public class TimeUtil {
	/**
	 * Formats a time span to german text.
	 * 
	 * @param text
	 *            The {@link StringBuilder} to append the time span to.
	 * @param interval
	 *            The time span, in milliseconds.
	 */
	public static void timeSpanString(StringBuilder text, int interval) {
		final short milliseconds = (short) (interval % 1000);
		interval -= milliseconds;
		interval /= 1000;
		final byte seconds = (byte) (interval % 60);
		interval -= seconds;
		interval /= 60;
		final byte minutes = (byte) (interval % 60);
		interval -= minutes;
		interval /= 60;
		final byte hours = (byte) (interval % 24);
		interval -= hours;
		interval /= 24;
		final long days = interval;
		byte nFields = 0;
		if (days != 0 && nFields < 2) {
			text.append(' ');
			text.append(days);
			text.append(" Tag");
			if (days != 1) {
				text.append('e');
			}
			nFields++;
		}
		if (hours != 0 && nFields < 2) {
			text.append(' ');
			text.append(hours);
			text.append(" Stunde");
			if (hours != 1) {
				text.append('n');
			}
			nFields++;
		}
		if (minutes != 0 && nFields < 2) {
			text.append(' ');
			text.append(minutes);
			text.append(" Minute");
			if (minutes != 1) {
				text.append('n');
			}
			nFields++;
		}
		if (seconds != 0 && nFields < 2) {
			text.append(' ');
			text.append(seconds);
			text.append(" Sekunde");
			if (seconds != 1) {
				text.append('n');
			}
			nFields++;
		}
		if (milliseconds != 0 && nFields < 2) {
			text.append(' ');
			text.append(milliseconds);
			text.append(" Millisekunde");
			if (milliseconds != 1) {
				text.append('n');
			}
			nFields++;
		}
	}
}
