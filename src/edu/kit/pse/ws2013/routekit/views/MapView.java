package edu.kit.pse.ws2013.routekit.views;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import edu.kit.pse.ws2013.routekit.controllers.MainController;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileCache;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileFinishedListener;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileSource;
import edu.kit.pse.ws2013.routekit.models.RouteModel;
import edu.kit.pse.ws2013.routekit.models.RouteModelListener;
import edu.kit.pse.ws2013.routekit.util.Coordinates;

/**
 * Displays a map section on the screen.
 * 
 * As a map projection, the Mercator projection is used.
 */
public class MapView extends JPanel implements MouseListener,
		MouseMotionListener, MouseWheelListener, TileFinishedListener,
		RouteModelListener {
	double x = 34297.855;
	double y = 22501.84;
	int zoom = 16;

	class ContextMenu extends JPopupMenu {
		JMenuItem start;
		JMenuItem target;

		public ContextMenu(final Coordinates coordinates) {
			start = new JMenuItem("Start hier");
			add(start);
			start.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					MainController.getInstance().setStartPoint(coordinates);
				}
			});
			target = new JMenuItem("Ziel hier");
			target.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					MainController.getInstance().setDestinationPoint(
							coordinates);

				}
			});
			add(target);
		}
	}

	TileSource source;
	private RouteModel rm;

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
	 * @param rm
	 *            RouteModel to display
	 */
	public MapView(TileSource source, RouteModel rm) {
		this.rm = rm;
		rm.addRouteListener(this);
		this.source = source;
		if (source instanceof TileCache) {
			((TileCache) source).addTileFinishedListener(this);
		}
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
				if (j < 0 || j >= 1 << zoom) {
					continue;
				}
				BufferedImage tile = source.renderTile(i & ((1 << zoom) - 1),
						j, zoom);
				g.drawImage(tile, (int) ((i - x) * 256), (int) ((j - y) * 256),
						null);
			}
		}
		Coordinates c = rm.getStart();
		if (c != null) {
			g.setColor(Color.RED);
			drawPoint(g, c);
		}
		c = rm.getDestination();
		if (c != null) {
			g.setColor(Color.GREEN);
			drawPoint(g, c);
		}
	}

	private void drawPoint(Graphics g, Coordinates c) {
		int it = 1 << zoom;
		int smtX = (int) ((c.getSmtX(zoom) - x) * 256f);
		int smtY = (int) ((c.getSmtY(zoom) - y) * 256f);
		for (double i = Math.floor(x / it); (i * it - x) * 256 < getWidth(); i++) {
			g.fillRect((int) (smtX + (i * it * 256) - 10), smtY - 10, 20, 20);
		}
	}

	int dx = 0;
	int dy = 0;
	double orgX;
	double orgY;

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
		if (e.isPopupTrigger()) {
			doPop(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		applyDrag(e);
		if (e.isPopupTrigger()) {
			doPop(e);
		}
	}

	private void doPop(MouseEvent e) {
		Coordinates coordinates = Coordinates.fromSmt(
				(float) (x + e.getX() / 256f), (float) (y + e.getY() / 256f),
				zoom);
		ContextMenu menu = new ContextMenu(coordinates);
		menu.show(e.getComponent(), e.getX(), e.getY());
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
		int limit = 1 << zoom;
		x = (x % limit + limit) % limit;
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int xp = e.getX();
		int yp = e.getY();
		double yZ = y + yp / 256f;
		double xZ = x + xp / 256f;
		int klick = e.getWheelRotation();
		if (klick == 0) {
			return;
		}
		while (klick > 0 && zoom < 19) {
			klick--;
			yZ *= 2;
			xZ *= 2;
			zoom++;
		}
		while (klick < 0 && zoom > 0) {
			klick++;
			yZ /= 2;
			xZ /= 2;
			zoom--;
		}

		y = yZ - yp / 256f;
		x = xZ - xp / 256f;
		repaint();
	}

	@Override
	public void tileFinished(int x, int y, int zoom, BufferedImage tile) {
		repaint();

	}

	@Override
	public void routeModelChanged() {
		repaint();
	}

}
