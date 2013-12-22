package edu.kit.pse.ws2013.routekit.mapdisplay;
import java.awt.image.BufferedImage;
/**
 * Abstrakte Klasse, die ein Interface für das (synchrone) Rendern von
 * Kartenkacheln definiert.
 */
public class TileSource {
	/**
	 * Berechnet die angegebene Kachel und gibt sie zurück.
	 * 
	 * @param x
	 *            siehe {@code x}
	 * @param y
	 *            siehe {@code y}
	 * @param zoom
	 *            siehe {@code zoom}
	 * @return
	 */
	public BufferedImage renderTile(int x, int y, int zoom) {
		return null;
	}
}
