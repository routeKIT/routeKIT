package edu.kit.pse.ws2013.routekit.mapdisplay;
import java.awt.image.BufferedImage;
/**
 * Wird benachrichtigt, wenn die Berechnung einer Kartenkachel abgeschlossen
 * ist.
 */
public class TileFinishedListener {
	/**
	 * Wird vom {@link TileCache} aufgerufen, wenn die Berechnung einer Kachel
	 * abgeschlossen ist. Die übliche Aktion ist, ein {@code repaint} der
	 * Kartenansicht im Graphical User Interface auszulösen.
	 * 
	 * @param x
	 *            siehe {@code x}
	 * @param y
	 *            siehe {@code y}
	 * @param zoom
	 *            siehe {@code zoom}
	 * @param tile
	 *            Die berechnete Kachel.
	 */
	public void tileFinished(int x, int y, int zoom, BufferedImage tile) {
	}
}
