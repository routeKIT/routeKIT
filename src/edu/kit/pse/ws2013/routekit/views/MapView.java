package edu.kit.pse.ws2013.routekit.views;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import edu.kit.pse.ws2013.routekit.mapdisplay.TileFinishedListener;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileSource;

/**
 * Displays a map section on the screen.
 * 
 * As a map projection, the Mercator projection is used.
 */
public class MapView extends JPanel implements MouseListener,
		MouseMotionListener, MouseWheelListener, TileFinishedListener {
	float x;
	float y;
	int zoom = 4;
	TileSource source;

	/**
	 * A constructor that creates a new MapView. The specified TileSource is
	 * used for rendering.
	 * 
	 * Because the tiles are requested synchronously with each MapView.paint(),
	 * source should be a TileCache.
	 * 
	 * @param source
	 *            An object that provides the map tiles, which are then
	 *            displayed.
	 */
	public MapView(TileSource source) {
		this.source = source;
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}

	/**
	 * Draws the currently visible map section. All visible tiles are requested
	 * simultaneously from source.
	 * 
	 * @param graphics
	 *            The Java Graphics, on which the map is drawn.
	 */
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		for (int i = (int) Math.floor(x); (i - x) * 256 < getWidth(); i++) {
			for (int j = (int) Math.floor(y); (j - y) * 256 < getHeight(); j++) {
				BufferedImage tile = source.renderTile(i & ((1 << zoom) - 1), j
						& ((1 << zoom) - 1), zoom);
				g.drawImage(tile, (int) ((i - x) * 256), (int) ((j - y) * 256),
						null);
			}
		}
	}

	int dx = 0;
	int dy = 0;
	float orgX;
	float orgY;

	private void applyDrag(MouseEvent e) {
		x = orgX - (e.getX() - dx) / 256f;
		y = orgY - (e.getY() - dy) / 256f;
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		dx = e.getX();
		dy = e.getY();
		orgX = x;
		orgY = y;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		applyDrag(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		applyDrag(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int xp = e.getX();
		int yp = e.getY();
		int klick = e.getWheelRotation();
		if (klick == 0) {
			return;
		}
		while (klick > 0) {
			klick--;
			// zoomIn;
		}
		while (klick < 0) {
			klick++;
			// zoomOut;
		}
		repaint();
	}

	@Override
	public void tileFinished(int x, int y, int zoom, BufferedImage tile) {
		repaint();

	}

}
