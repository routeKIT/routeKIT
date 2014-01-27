package edu.kit.pse.ws2013.routekit.mapdisplay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Manages the calculation of map tiles and caches them. Tiles can be requested,
 * and after the (asynchronous) calculation is finished, registered
 * {@link TileFinishedListener TileFinishedListeners} are notified.
 * <p>
 * Internally, cached tiles are held in a way that permits the Garbage Collector
 * to discard them in the case of memory shortage (via {@link SoftReference
 * SoftReferences}).
 */
public class TileCache implements TileSource {
	private class TileJob implements Runnable {
		private int x;
		private int y;
		private int zoom;

		public TileJob(int x, int y, int zoom) {
			this.x = x;
			this.y = y;
			this.zoom = zoom;
		}

		@Override
		public void run() {
			if (zoom < 0 || zoom > 19) {
				return;
			}
			String key = key(x, y, zoom);
			if (cache.containsKey(key) && cache.get(key).get() != null) {
				return;
			}
			BufferedImage result = target.renderTile(x, y, zoom);
			cache.put(key, new SoftReference<BufferedImage>(result));
			fireListeners(x, y, zoom, result);
		}
	}

	private class Worker extends Thread {

		public Worker(String name) {
			super(name);
			setDaemon(true);
		}

		@Override
		public void run() {
			while (running) {
				while (waiting.size() > 100) {// too much work
					waiting.removeLast();
				}
				while (prefetchWaiting.size() > 100) {// too much work
					prefetchWaiting.removeLast();
				}
				try {
					// returns null if empty
					TileJob job = waiting.pollFirst();
					if (job == null) {
						// immediately returns null if empty
						job = prefetchWaiting.pollFirst(200,
								TimeUnit.MILLISECONDS);
					}
					if (job != null) {
						job.run();
					}
				} catch (InterruptedException e) {
					// its ok...
				}
			}
		}
	}

	private boolean running = true;
	private LinkedBlockingDeque<TileJob> waiting = new LinkedBlockingDeque<>();
	private LinkedBlockingDeque<TileJob> prefetchWaiting = new LinkedBlockingDeque<>();

	private LinkedList<TileFinishedListener> listeners = new LinkedList<>();
	private TileSource target;
	private Map<String, SoftReference<BufferedImage>> cache = Collections
			.synchronizedMap(new HashMap<String, SoftReference<BufferedImage>>());

	private Worker[] workers;
	BufferedImage tile;

	/**
	 * Creates a new {@link TileCache} which uses the given {@link TileSource}
	 * for calculating tiles.
	 * 
	 * @param target
	 *            The {@link TileSource} that actually renders the tiles, and
	 *            whose results are cached.
	 */
	public TileCache(TileSource target) {
		this.target = target;
		int workerCount = Runtime.getRuntime().availableProcessors();
		workers = new Worker[workerCount];
		for (int i = 0; i < workerCount; i++) {
			workers[i] = new Worker("TileCache Worker " + i);
			workers[i].start();
		}
		tile = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
		Graphics g = tile.createGraphics();
		g.setColor(Color.gray);
		g.fillRect(1, 1, 254, 254);
	}

	/**
	 * If the requested tile is cached and available, it is returned directly;
	 * otherwise, a “dummy” tile is returned and the correct one is in turn
	 * requested (asynchronously) from the internal {@link TileSource}. When the
	 * calculation is finished, the resulting tile is cached and registered
	 * {@link TileFinishedListener TileFinishedListeners} are notified. In any
	 * case, this method returns immediately.
	 * <p>
	 * Additionally, tiles surrounding the requested tile (in all 6 directions)
	 * are also requested and cached (with lower priority), since it is likely
	 * that they will be requested soon as well (locality of reference).
	 * 
	 * @param x
	 *            The SMT X component.
	 * @param y
	 *            The SMT Y component.
	 * @param zoom
	 *            The zoom level.
	 * @return Either a dummy tile or a cached tile.
	 */
	@Override
	public BufferedImage renderTile(int x, int y, int zoom) {
		final int bitmask = (1 << zoom) - 1;
		x &= bitmask;
		y &= bitmask;
		String key = key(x, y, zoom);
		SoftReference<BufferedImage> cacheVal = cache.get(key);
		BufferedImage tile;
		if (cacheVal != null && (tile = cacheVal.get()) != null) {
			prefetchEnv(x, y, zoom);
			return tile;
		}
		waiting.addFirst(new TileJob(x, y, zoom));
		prefetchEnv(x, y, zoom);
		return this.tile;
	}

	private void prefetchEnv(int x, int y, int zoom) {
		prefetch(x + 1, y, zoom);
		prefetch(x - 1, y, zoom);
		prefetch(x, y + 1, zoom);
		prefetch(x, y - 1, zoom);
		if (zoom > 0) {
			prefetch(x / 2, y / 2, zoom - 1);
		}
		if (zoom < 19) {
			prefetch(x * 2, y * 2, zoom + 1);
			prefetch(x * 2 + 1, y * 2, zoom + 1);
			prefetch(x * 2 + 1, y * 2 + 1, zoom + 1);
			prefetch(x * 2, y * 2 + 1, zoom + 1);
		}
	}

	private void prefetch(int x, int y, int zoom) {
		final int bitmask = (1 << zoom) - 1;
		x &= bitmask;
		y &= bitmask;
		SoftReference<BufferedImage> ref = cache.get(key(x, y, zoom));
		if (ref == null || ref.get() == null) {
			prefetchWaiting.addFirst(new TileJob(x, y, zoom));
		}
	}

	/**
	 * Registers a {@link TileFinishedListener} that is notified when
	 * calculation of a tile has finished. The tile itself is part of the
	 * notification.
	 * 
	 * @param listener
	 *            The listener that shall be added.
	 */
	public void addTileFinishedListener(TileFinishedListener listener) {
		listeners.add(listener);
	}

	private void fireListeners(int x, int y, int zoom, BufferedImage tile) {
		for (TileFinishedListener list : listeners) {
			list.tileFinished(x, y, zoom, tile);

		}
	}

	public TileSource getTarget() {
		return target;
	}

	private static String key(int x, int y, int zoom) {
		return x + "/" + y + "/" + zoom;
	}

	/**
	 * Causes this TileCache to stop fetching.
	 */
	public void stop() {
		running = false;
		for (int i = 0; i < workers.length; i++) {
			workers[i].interrupt();
		}
	}

	/**
	 * Causes this TileCache to stop fetching and waits for total termination.
	 * 
	 * @throws InterruptedException
	 *             if the waiting is interrupted. if tw
	 */
	public void waitForStop() throws InterruptedException {
		stop();
		for (int i = 0; i < workers.length; i++) {
			workers[i].join();
		}
	}
}
