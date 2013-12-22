package edu.kit.pse.ws2013.routekit.mapdisplay;
import java.awt.image.BufferedImage;
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
public class TileCache {
	/**
	 * Konstruktor: Erstellt einen neuen Cache für die angegebene
	 * {@link TileSource}.
	 * 
	 * @param target
	 *            Die {@code TileSource}, die die tatsächliche Berechnung
	 *            durchführt und deren Ergebnisse zwischengespeichert werden.
	 */
	public TileCache(TileSource target) {
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
	 *            Die -X-Komponente.
	 * @param y
	 *            Die -Y-Komponente.
	 * @param zoom
	 *            Die Zoomstufe.
	 * @return
	 */
	public BufferedImage renderTile(int x, int y, int zoom) {
		return null;
	}
	/**
	 * Registriert einen {@link TileFinishedListener}, der benachrichtigt wird,
	 * wenn eine Kachel fertig berechnet ist. Die Kachel ist Teil der Nachricht.
	 * 
	 * @param listener
	 *            Der Listener, der hinzugefügt werden soll.
	 */
	public void addTileFinishedListener(TileFinishedListener listener) {
	}
}
