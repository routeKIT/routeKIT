package edu.kit.pse.ws2013.routekit.mapdisplay;

import java.awt.Component;
import java.awt.image.BufferedImage;

/**
 * Gets notified when calculation of a map tile finishes.
 */
public interface TileFinishedListener {
	/**
	 * Called by the {@link TileCache} when the calculation of a tile finishes.
	 * The typical action is to trigger a {@link Component#repaint() repaint} of
	 * the Graphical User Interface.
	 * 
	 * @param x
	 *            The SMT X coordinate of the finished tile.
	 * @param y
	 *            The SMT Y coordinate of the finished tile.
	 * @param zoom
	 *            The zoom of the finished tile.
	 * @param tile
	 *            The calculated tile.
	 */
	public void tileFinished(int x, int y, int zoom, BufferedImage tile);
}
