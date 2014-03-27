package edu.kit.pse.ws2013.routekit.mapdisplay;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

/**
 * A {@link TileSource} that downloads the tiles from the OpenStreetMap servers.
 */
public class OSMRenderer implements TileSource {

	private final Matcher matcher;
	private final Random random;
	private final String ext;

	/**
	 * Creates a new {@link OSMRenderer}.
	 * 
	 * @param tileServer
	 *            The tile server to use. The string may contain regex-like
	 *            groups to account for load balancing, like this:
	 *            {@code http://[abc].tile.openstreetmap.org/}
	 * @throws IOException
	 *             If the server can’t be reached.
	 */
	public OSMRenderer(String tileServer) throws IOException {
		if (tileServer == null) {
			throw new IllegalArgumentException("tileServer must not be null!");
		}
		if (!tileServer.endsWith("/")) {
			tileServer += "/";
		}
		matcher = Pattern.compile("\\[[^\\]]+\\]").matcher(tileServer);
		random = new Random();

		// find out what extension we need
		// try .png first
		URL uPng = new URL(getTileServer() + "0/0/0.png");
		HttpURLConnection cPng = (HttpURLConnection) uPng.openConnection();
		cPng.setRequestMethod("HEAD"); // we’re not interested in the content
		cPng.connect();
		if (cPng.getResponseCode() == 200 /* OK */) {
			ext = ".png";
		} else {
			// try .jpg instead
			URL uJpg = new URL(getTileServer() + "0/0/0.jpg");
			HttpURLConnection cJpg = (HttpURLConnection) uJpg.openConnection();
			cJpg.setRequestMethod("HEAD");
			cJpg.connect();
			if (cJpg.getResponseCode() == 200) {
				ext = ".jpg";
			} else {
				throw new IOException(
						"No tiles found with .jpg or .png suffixes!");
			}
		}
	}

	private synchronized String getTileServer() {
		final StringBuffer s = new StringBuffer();
		while (matcher.find()) {
			// choose a random item to transform
			// [abcd].server.com into
			// c.server.com
			final String group = matcher.group();
			final String characters = group.substring(1, group.length() - 1);
			matcher.appendReplacement(
					s,
					new String(new char[] { characters.charAt(random
							.nextInt(characters.length())) }));
		}
		matcher.appendTail(s);
		matcher.reset();
		return s.toString();
	}

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
			URL url = new URL(getTileServer() + zoom + "/" + x + "/" + y + ext);
			HttpURLConnection huc = ((HttpURLConnection) url.openConnection());
			huc.setRequestProperty("User-Agent", "routeKit/1.0 (study project)");
			return ImageIO.read(huc.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
