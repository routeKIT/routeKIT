package test.edu.kit.pse.ws2013.routekit.history;

import static org.junit.Assert.*;
import static test.edu.kit.pse.ws2013.routekit.history.TestHistoryEntry.assertHistoryEntryEquals;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.junit.Test;

import edu.kit.pse.ws2013.routekit.history.History;
import edu.kit.pse.ws2013.routekit.history.HistoryEntry;
import edu.kit.pse.ws2013.routekit.util.Coordinates;

public class TestHistory {

	@Test
	public void testSaveLoad() throws IOException {
		History h1 = new History();
		h1.addEntry(new Coordinates(0, 0), new Coordinates(1, 1));
		h1.addEntry(new Coordinates(11, 38), new Coordinates(42, 8));
		File f = File.createTempFile("routeKit_testHistory_", "hst");
		h1.save(f);
		History h2 = History.load(f);
		assertHistoryEquals(h1, h2);
	}
	
	public static void assertHistoryEquals(History expected, History actual) {
		Iterator<HistoryEntry> iExpected = expected.getEntries().iterator();
		Iterator<HistoryEntry> iActual = actual.getEntries().iterator();
		while(iExpected.hasNext()) {
			assertTrue("not enough entries!", iActual.hasNext());
			assertHistoryEntryEquals(iExpected.next(), iActual.next());
		}
		assertFalse("too many entries!", iActual.hasNext());
	}
}
