package edu.kit.pse.ws2013.routekit.mapdisplay;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * Eine {@link TileSource}, die die OSM-Kachel vom OpenStreetMap-Server
 * herunterlädt.
 */
public class OSMRenderer implements TileSource {
	/**
	 * Lädt die angegebene Kachel herunter und gibt sie zurück.
	 * 
	 * @param x
	 *            siehe {@code x}
	 * @param y
	 *            siehe {@code y}
	 * @param zoom
	 *            siehe {@code zoom}
	 * @return
	 */
	@Override
	public BufferedImage renderTile(int x, int y, int zoom) {
		if (zoom > 19 || zoom < 0) {
			return null;
		}
		try {
			URL url = new URL("http://c.tile.openstreetmap.org/" + zoom + "/"
					+ x + "/" + y + ".png");
			HttpURLConnection huc = ((HttpURLConnection) url.openConnection());
			huc.setRequestProperty("User-Agent", "routeKit/1.0 (study project)");
			return ImageIO.read(huc.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
