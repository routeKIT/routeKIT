package views;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import mapDisplay.TileCache;
import mapDisplay.TileSource;
/**
 * Zeigt einen Kartenausschnitt auf dem Bildschirm an.
 * 
 * Als Kartenprojektion wird die Mercator-Projektion verwendet.
 */
public class MapView extends JPanel {
	/**
	 * Zeichnet den aktuell sichtbaren Kartenausschnitt. Alle sichtbaren Kacheln
	 * werden von {@code source} synchron angefordert.
	 * 
	 * @param graphics
	 *            Die Java {@code Graphics}, auf welche die Karte gezeichnet
	 *            wird.
	 */
	@Override
	public void paint(Graphics graphics) {
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, getWidth(), getHeight());
		graphics.translate(getWidth() / 2, getHeight() / 2);
		((Graphics2D) graphics).rotate(45);
		graphics.setColor(Color.BLACK);
		graphics.setFont(new Font(Font.SANS_SERIF, 0, 20));
		graphics.drawString("Ich bin ein Karte", -100, 0);
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
