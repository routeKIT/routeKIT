package test.edu.kit.pse.ws2013.routekit.mapdisplay;

import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileCache;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileSource;

public class TestTileCache {
	class DummySource implements TileSource {
		@Override
		public BufferedImage renderTile(int x, int y, int zoom) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return new BufferedImage(256, 256, BufferedImage.TYPE_4BYTE_ABGR);
		}
	}

	TileCache tc = null;
	BufferedImage dummy = null;

	@Before
	public void setUp() throws Exception {
		tc = new TileCache(new DummySource());
		dummy = tc.renderTile(0, 0, 1);
	}

	private void assertDummy(BufferedImage image) {
		assertEquals(dummy, image);
	}

	private void assertNotDummy(BufferedImage image) {
		assertNotEquals(dummy, image);
	}

	@Test
	public void testCaching() throws InterruptedException {
		assertDummy(tc.renderTile(0, 0, 1));
		Thread.sleep(100);
		assertNotDummy(tc.renderTile(0, 0, 1));
	}

	@Test
	public void testPrefetch() throws InterruptedException {
		assertDummy(tc.renderTile(0, 0, 5));
		assertDummy(tc.renderTile(0, 1, 5));
		assertDummy(tc.renderTile(1, 0, 5));
		assertDummy(tc.renderTile(0, -1, 5));
		assertDummy(tc.renderTile(-1, 0, 5));
		assertDummy(tc.renderTile(0, 0, 4));
		assertDummy(tc.renderTile(0, 0, 6));
		Thread.sleep(100);
		assertNotDummy(tc.renderTile(0, 0, 5));
		assertNotDummy(tc.renderTile(0, 1, 5));
		assertNotDummy(tc.renderTile(1, 0, 5));
		assertNotDummy(tc.renderTile(0, -1, 5));
		assertNotDummy(tc.renderTile(-1, 0, 5));
		assertNotDummy(tc.renderTile(0, 0, 4));
		assertNotDummy(tc.renderTile(0, 0, 6));
	}

	@Test
	public void testThrowAway() throws InterruptedException {
		assertDummy(tc.renderTile(0, 0, 10));
		Thread.sleep(50);
		assertNotDummy(tc.renderTile(0, 0, 10));
		for (int x = 0; x < 50; x++) {
			for (int y = 0; y < 50; y++) {
				tc.renderTile(x, y, 9);
				Thread.sleep(1);
			}
		}
		System.gc();
		assertDummy(tc.renderTile(0, 0, 10));
	}
}
