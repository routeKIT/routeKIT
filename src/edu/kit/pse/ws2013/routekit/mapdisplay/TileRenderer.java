package edu.kit.pse.ws2013.routekit.mapdisplay;

import java.awt.image.BufferedImage;

import edu.kit.pse.ws2013.routekit.map.Graph;

/**
 * Eine {@link TileSource}, die die Kacheln selbst berechnet.
 */
public class TileRenderer implements TileSource {
	/**
	 * Konstruktor: Erzeugt einen neuen {@code TileRenderer}.
	 * 
	 * @param graph
	 *            Ein Adjazenzfeld.
	 */
	public TileRenderer(Graph graph) {
	}

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
