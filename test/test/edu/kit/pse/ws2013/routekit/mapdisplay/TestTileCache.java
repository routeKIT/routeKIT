package test.edu.kit.pse.ws2013.routekit.mapdisplay;

import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileCache;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileFinishedListener;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileSource;

public class TestTileCache {
	class WaitingSource implements TileSource {

		@Override
		public BufferedImage renderTile(int x, int y, int zoom) {
			synchronized (TestTileCache.this) {
				return new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
			}
		}

	}

	@Before
	public void setUp() throws Exception {
	}

	Throwable failed = null;

	@Test
	public void test() throws Throwable {
		final TileCache tc = new TileCache(new WaitingSource());
		tc.addTileFinishedListener(new TileFinishedListener() {
			int last = -1;

			@Override
			public void tileFinished(int x, int y, int zoom, BufferedImage tile) {
				try {
					if (last == -1) {
						assertEquals(0, x);
						last = 120;
					} else if (last > 20) {
						last--;
						assertEquals(last, x);
					}
					assertEquals(tile, tc.renderTile(x, y, zoom));
				} catch (Throwable t) {
					failed = t;
				}
			}
		});
		synchronized (this) {
			assertNull(tc.renderTile(0, 0, 19));
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (int i = 1; i < 120; i++) {
				assertNull(tc.renderTile(i, 0, 19));
			}
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertNotNull(tc.renderTile(0, 0, 19));
		assertNull(tc.renderTile(1, 0, 19));
		assertNull(tc.renderTile(2, 0, 19));
		assertNull(tc.renderTile(3, 0, 19));
		assertNotNull(tc.renderTile(50, 0, 19));
		tc.waitForStop();
		if (failed != null) {
			throw failed;
		}
	}

}
