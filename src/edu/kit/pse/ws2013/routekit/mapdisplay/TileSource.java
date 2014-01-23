package edu.kit.pse.ws2013.routekit.mapdisplay;

import java.awt.image.BufferedImage;

/**
 * Interface for (synchronous) rendering of a Slippy Map Tile.
 */
public interface TileSource {
	/**
	 * Calculates the given tile and returns it.
	 * 
	 * @param x
	 *            The SMT X coordinate of the requested tile.
	 * @param y
	 *            The SMT Y coordinate of the requested tile.
	 * @param zoom
	 *            The zoom of the requested tile.
	 * @param tile
	 *            The calculated tile.
	 */
	public BufferedImage renderTile(int x, int y, int zoom);
}
