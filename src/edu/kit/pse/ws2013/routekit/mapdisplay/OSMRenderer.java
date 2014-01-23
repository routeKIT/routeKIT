package edu.kit.pse.ws2013.routekit.mapdisplay;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * A {@link TileSource} that downloads the tiles from the OpenStreetMap servers.
 */
public class OSMRenderer implements TileSource {
	/**
	 * Downloads the given tile and returns it.
	 * 
	 * @param x
	 *            The SMT X coordinate of the requested tile.
	 * @param y
	 *            The SMT Y coordinate of the requested tile.
	 * @param zoom
	 *            The zoom of the requested tile.
	 * @param tile
	 *            The downloaded tile.
	 */
	@Override
	public BufferedImage renderTile(int x, int y, int zoom) {
		if (zoom > 19 || zoom < 0) {
			return null;
		}
		try {
			// TODO we’re not allowed to hardcode this URL!
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
