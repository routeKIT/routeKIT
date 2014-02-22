package edu.kit.pse.ws2013.routekit.history;

import static edu.kit.pse.ws2013.routekit.util.TestCoordinates.assertCoordinatesEquals;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import edu.kit.pse.ws2013.routekit.util.Coordinates;

public class TestHistoryEntry {

	@Test
	public void testToFromString() {
		float startLat = 49.013766f;
		float startLon = 8.419944f;
		float destLat = 11.38f;
		float destLon = 4.735f;
		Date date = new Date();
		Coordinates start = new Coordinates(startLat, startLon);
		Coordinates dest = new Coordinates(destLat, destLon);
		HistoryEntry entry = new HistoryEntry(start, dest, date);
		String s = entry.toString();
		HistoryEntry parsed = HistoryEntry.fromString(s);
		assertHistoryEntryEquals(entry, parsed);
	}

	public static void assertHistoryEntryEquals(HistoryEntry expected,
			HistoryEntry actual) {
		assertCoordinatesEquals(expected.getStart(), actual.getStart());
		assertCoordinatesEquals(expected.getDest(), actual.getDest());
		assertEquals(expected.getDate().getTime(), actual.getDate().getTime(),
				1000.0);
	}
}
