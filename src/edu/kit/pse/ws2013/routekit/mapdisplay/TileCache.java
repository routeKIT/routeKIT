package edu.kit.pse.ws2013.routekit.mapdisplay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Verwaltet die Berechnung von Kartenkacheln und ist ein Zwischenspeicher für
 * diese. Kacheln können angefragt werden, und nachdem die (asynchrone)
 * Berechnung abgeschlossen ist, werden registrierte
 * {@link TileFinishedListener} benachrichtigt.
 * 
 * Intern werden die zwischengespeicherten Kacheln so gehalten, dass der Garbage
 * Collector sie bei Speicherknappheit verwerfen kann (etwa durch
 * {@code SoftReference}s).
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
			BufferedImage result = target.renderTile(x, y, zoom);
			map.put(key(x, y, zoom), new SoftReference<BufferedImage>(result));
			fireListeners(x, y, zoom, result);
		}
	}

	private class Worker extends Thread {
		@Override
		public void run() {
			while (running) {
				while (waiting.size() > 100) {// too much work
					waiting.removeLast();
				}
				try {
					waiting.takeFirst().run();
				} catch (InterruptedException e) {
					// its ok...
				}
			}
		}
	}

	private boolean running = true;
	private LinkedBlockingDeque<TileJob> waiting = new LinkedBlockingDeque<>();

	private LinkedList<TileFinishedListener> listeners = new LinkedList<>();
	private TileSource target;
	private HashMap<String, SoftReference<BufferedImage>> map = new HashMap<>();

	private Worker worker;
	BufferedImage tile;

	/**
	 * Konstruktor: Erstellt einen neuen Cache für die angegebene
	 * {@link TileSource}.
	 * 
	 * @param target
	 *            Die {@code TileSource}, die die tatsächliche Berechnung
	 *            durchführt und deren Ergebnisse zwischengespeichert werden.
	 */
	public TileCache(TileSource target) {
		this.target = target;
		worker = new Worker();
		worker.start();
		tile = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
		Graphics g = tile.createGraphics();
		g.setColor(Color.gray);
		g.fillRect(0, 0, 255, 255);
	}

	/**
	 * Ist die angeforderte Kachel bereits im Zwischenspeicher vorhanden, so
	 * wird sie direkt zurückgegeben; andernfalls wird eine Dummy-Kachel
	 * zurückgegeben und die richtige von {@code target} angefordert, im
	 * Zwischenspeicher gespeichert und dann zurückgegeben. Kacheln von tieferer
	 * Zoomstufe und der Umgebung einer Kachel werden von {@code target}
	 * angefordert und im Zwischenspeicher gespeichert.
	 * 
	 * @param x
	 *            Die SMT-X-Komponente.
	 * @param y
	 *            Die SMT-Y-Komponente.
	 * @param zoom
	 *            Die Zoomstufe.
	 * @return
	 */
	public BufferedImage renderTile(int x, int y, int zoom) {
		String key = key(x, y, zoom);
		SoftReference<BufferedImage> cacheVal = map.get(key);
		if (cacheVal != null && cacheVal.get() != null) {
			return cacheVal.get();
		}
		waiting.addFirst(new TileJob(x, y, zoom));
		return tile;
	}

	/**
	 * Registriert einen {@link TileFinishedListener}, der benachrichtigt wird,
	 * wenn eine Kachel fertig berechnet ist. Die Kachel ist Teil der Nachricht.
	 * 
	 * @param listener
	 *            Der Listener, der hinzugefügt werden soll.
	 */
	public void addTileFinishedListener(TileFinishedListener listener) {
		listeners.add(listener);
	}

	private void fireListeners(int x, int y, int zoom, BufferedImage tile) {
		for (TileFinishedListener list : listeners) {
			list.tileFinished(x, y, zoom, tile);

		}
	}

	private static String key(int x, int y, int zoom) {
		return x + "/" + y + "/" + zoom;
	}

	/**
	 * Causes this TileCache to stop fetching.
	 */
	public void stop() {
		running = false;
		worker.interrupt();
	}

	/**
	 * Causes this TileCache to stop fetching and waits for total termination.
	 * 
	 * @throws InterruptedException
	 *             if the waiting is interrupted. if tw
	 */
	public void waitForStop() throws InterruptedException {
		stop();
		worker.join();
	}
}
