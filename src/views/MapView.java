package views;
import java.awt.Graphics;

import mapDisplay.TileCache;
import mapDisplay.TileSource;
/**
 * Zeigt einen Kartenausschnitt auf dem Bildschirm an.
 * 
 * Als Kartenprojektion wird die Mercator-Projektion verwendet.
 */
public class MapView {
	/**
	 * Zeichnet den aktuell sichtbaren Kartenausschnitt. Alle sichtbaren Kacheln
	 * werden von {@code source} synchron angefordert.
	 * 
	 * @param graphics
	 *            Die Java {@code Graphics}, auf welche die Karte gezeichnet
	 *            wird.
	 */
	public void paint(Graphics graphics) {
	}
	/**
	 * Konstruktor: Erzeugt eine neue {@link MapView}. Die angegebene
	 * {@link TileSource} wird zum Rendern verwendet.
	 * 
	 * Da die Kacheln bei jedem {@link MapView#paint} synchron angefragt werden,
	 * sollte {@code source} ein {@link TileCache} sein.
	 * 
	 * @param source
	 *            Ein Objekt, das die Kartenkacheln liefert, die dann angezeigt
	 *            werden.
	 */
	public MapView(TileSource source) {
	}
}
