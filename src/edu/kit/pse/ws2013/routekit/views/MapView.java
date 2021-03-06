package edu.kit.pse.ws2013.routekit.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.Window;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import edu.kit.pse.ws2013.routekit.controllers.MainController;
import edu.kit.pse.ws2013.routekit.map.Graph;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileCache;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileFinishedListener;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileSource;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;
import edu.kit.pse.ws2013.routekit.models.RouteModel;
import edu.kit.pse.ws2013.routekit.models.RouteModelListener;
import edu.kit.pse.ws2013.routekit.routecalculation.Route;
import edu.kit.pse.ws2013.routekit.util.Coordinates;

/**
 * Displays a map section on the screen.
 * 
 * As a map projection, the Mercator projection is used.
 */
public class MapView extends JPanel implements MouseListener,
		MouseMotionListener, MouseWheelListener, TileFinishedListener,
		RouteModelListener, ActionListener {
	private static final long serialVersionUID = 1L;
	double x = 34297.855;
	double y = 22501.84;
	int zoom = 16;

	class ContextMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;
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

	final BufferedImage flagPattern;

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
		in.addActionListener(this);
		out.addActionListener(this);
		in.setPreferredSize(new Dimension(45, 30));
		out.setPreferredSize(new Dimension(45, 30));
		add(in);
		add(out);
		add(startCalc);
		startCalc.setVisible(false);
		startCalc.setFont(new Font("Arial", Font.BOLD, 20));
		startCalc.setBackground(Color.RED);
		startCalc.setForeground(Color.WHITE);
		startCalc.setSize(300, 200);
		startCalc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Container parentWindow = MapView.this.getParent();
				while (!(parentWindow instanceof Window)) {
					parentWindow = parentWindow.getParent();
				}
				ProgressDialog p = new ProgressDialog((Window) parentWindow,
						true);
				ProgressReporter reporter = new ProgressReporter();
				reporter.addProgressListener(p);
				reporter.pushTask("Speichere Änderungen");
				MainController.getInstance().startPrecalculation(reporter);
				p.setVisible(true);
			}
		});

		flagPattern = new BufferedImage(10, 10, BufferedImage.TYPE_BYTE_BINARY);
		final Graphics g = flagPattern.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, 5, 5);
		g.fillRect(5, 5, 5, 5);
		g.setColor(Color.black);
		g.fillRect(0, 5, 5, 5);
		g.fillRect(5, 0, 5, 5);
	}

	public void setTileSource(TileSource source) {
		this.source = source;
		if (source instanceof TileCache) {
			((TileCache) source).addTileFinishedListener(this);
		}
		repaint();
	}

	JButton startCalc = new JButton("<html>Vorberechnung starten</html>");

	JButton in = new JButton("+");
	JButton out = new JButton("-");

	/**
	 * Draws the currently visible map section. All visible tiles are requested
	 * simultaneously from source.
	 * 
	 * @param graphics
	 *            The Java Graphics, on which the map is drawn.
	 */
	@Override
	public void paint(Graphics graphics) {
		in.setLocation(getWidth() - in.getWidth() - 10, 10);
		out.setLocation(getWidth() - out.getWidth() - 10, 20 + in.getHeight());
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, getWidth(), getHeight());
		for (int i = (int) Math.floor(x); (i - x) * 256 < getWidth(); i++) {
			for (int j = (int) Math.floor(y); (j - y) * 256 < getHeight(); j++) {
				if (j < 0 || j >= 1 << zoom) {
					continue;
				}
				BufferedImage tile = source.renderTile(i & ((1 << zoom) - 1),
						j, zoom);
				if (!isEnabled()) {
					ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
					ColorConvertOp op = new ColorConvertOp(cs, null);
					tile = op.filter(tile, null);
				}
				graphics.drawImage(tile, (int) ((i - x) * 256),
						(int) ((j - y) * 256), null);
			}
		}
		if (!isEnabled()) {
			startCalc.setBounds((getWidth() - 300) / 2,
					(getHeight() - 200) / 2, 300, 200);
			super.paintComponents(graphics);

			return;
		}
		final Route r = rm.getCurrentRoute();
		if (r != null) {
			drawRoute(graphics, r);
		}
		Coordinates c = rm.getStart();
		if (c != null) {
			graphics.setColor(Color.RED);
			drawPoint(graphics, c, false);
		}
		c = rm.getDestination();
		if (c != null) {
			graphics.setColor(Color.GREEN);
			drawPoint(graphics, c, true);
		}
		super.paintComponents(graphics);
	}

	private void drawRoute(Graphics g, final Route r) {
		g.setColor(new Color(255, 0, 0, 192));
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Stroke s = g2.getStroke();
		g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		int[] xPoints = new int[r.getTurns().size() + 2];
		int[] yPoints = new int[xPoints.length];
		int i = 0;
		for (Coordinates co : r) {
			xPoints[i] = (int) ((co.getSmtX(zoom) - x) * 256);
			yPoints[i] = (int) ((co.getSmtY(zoom) - y) * 256);
			i++;
		}
		g.drawPolyline(xPoints, yPoints, xPoints.length);

		Stroke stroke = new BasicStroke(6, // Width
				BasicStroke.CAP_ROUND, // End cap
				BasicStroke.JOIN_ROUND, // Join style
				10.0f, // Miter limit
				new float[] { 5.0f, 10.0f }, // Dash pattern
				0.0f); // Dash phase
		g2.setStroke(stroke);
		int startEdge = r.getStart().getEdge();
		int destEdge = r.getDestination().getEdge();
		Graph graph = r.getData().getStreetMap().getGraph();
		Coordinates startOnEdge = graph.getCoordinates(
				graph.getStartNode(startEdge)).goIntoDirection(
				graph.getCoordinates(graph.getTargetNode(startEdge)),
				r.getStart().getPosition());
		Coordinates destOnEdge = graph.getCoordinates(
				graph.getStartNode(destEdge)).goIntoDirection(
				graph.getCoordinates(graph.getTargetNode(destEdge)),
				r.getDestination().getPosition());
		g2.drawLine((int) ((rm.getStart().getSmtX(zoom) - x) * 256), (int) ((rm
				.getStart().getSmtY(zoom) - y) * 256), (int) ((startOnEdge
				.getSmtX(zoom) - x) * 256),
				(int) ((startOnEdge.getSmtY(zoom) - y) * 256));
		g2.drawLine((int) ((rm.getDestination().getSmtX(zoom) - x) * 256),
				(int) ((rm.getDestination().getSmtY(zoom) - y) * 256),
				(int) ((destOnEdge.getSmtX(zoom) - x) * 256),
				(int) ((destOnEdge.getSmtY(zoom) - y) * 256));
		g2.setStroke(s);
	}

	private void drawPoint(Graphics g, Coordinates c, final boolean checkered) {
		int it = 1 << zoom;
		int smtX = (int) ((c.getSmtX(zoom) - x) * 256f);
		int smtY = (int) ((c.getSmtY(zoom) - y) * 256f);
		for (double i = Math.floor(x / it); (i * it - x) * 256 < getWidth(); i++) {
			int x = (int) (smtX + (i * it * 256));
			int y = smtY;
			g.translate(x, y);
			if (checkered && g instanceof Graphics2D) {
				((Graphics2D) g).setPaint(new TexturePaint(flagPattern,
						new Rectangle2D.Float(1, -2, 10, 10)));
			}
			drawFlag(g);
			g.translate(-x, -y);
		}
	}

	private static void drawFlag(Graphics g) {
		// @formatter:off
		// ===================================    –
		// |------------flagWidth------------|    |
		// |                                 |    |
		// |                                 |    |
		// |                                 |    |
		// |                                 |    |
		// |          |-coneWidth-|          |    |
		// ===========             ===========    | flagHeight
		//            \           /  |            |
		//             \         /   |            |
		//              \       /    |            |
		//               \     /     | coneHeight |
		//                \   /      |            |
		//                 \ /       |            |
		//                  V        –            –
		//                (0|0)
		// @formatter:on
		final int coneWidth = 8;
		final int coneHeight = 10;
		final int flagWidth = 20;
		final int flagHeight = 28;

		final int coneRadius = coneWidth / 2;
		final int halfFlagWidth = flagWidth / 2;
		Polygon p = new Polygon(new int[] { 0, -coneRadius, -halfFlagWidth,
				-halfFlagWidth, halfFlagWidth, halfFlagWidth, coneRadius },
				new int[] { 0, -coneHeight, -coneHeight, -flagHeight,
						-flagHeight, -coneHeight, -coneHeight }, 7);
		g.fillPolygon(p);
		Color c = g.getColor();
		g.setColor(Color.BLACK);
		g.drawPolygon(p);
		g.setColor(c);
	}

	int dx = -1;
	int dy = -1;
	int ozoom;
	double orgX;
	double orgY;

	private void applyDrag(MouseEvent e) {
		x = orgX - (e.getX() - dx) / 256f;
		y = orgY - (e.getY() - dy) / 256f;
		int limit = 1 << zoom;
		x = (x % limit + limit) % limit;
		zoom = ozoom;
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() % 2 == 0 && !e.isConsumed()) {
			e.consume();
			mouseWheelMoved(new MouseWheelEvent(this, 0,
					System.currentTimeMillis(), 0, e.getX(), e.getY(), 2,
					false, 0, 1, -1));
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!isEnabled()) {
			return;
		}
		if (e.isPopupTrigger()) {
			doPop(e);
		} else if (e.getButton() == MouseEvent.BUTTON1) {
			dx = e.getX();
			dy = e.getY();
			ozoom = zoom;
			orgX = x;
			orgY = y;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!isEnabled()) {
			return;
		}
		if (e.isPopupTrigger()) {
			doPop(e);
		} else if (e.getButton() == MouseEvent.BUTTON1) {
			applyDrag(e);
		}
		dx = -1;
		dy = -1;
	}

	private void doPop(MouseEvent e) {
		float y2 = (float) (y + e.getY() / 256f);
		if (y2 < 0 || y2 > 1 << zoom) {
			return;
		}
		Coordinates coordinates = Coordinates.fromSmt(
				(float) (x + e.getX() / 256f), y2, zoom);
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
		if (!isEnabled()) {
			return;
		}
		if (dx == -1) {
			return;
		}
		applyDrag(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!isEnabled() || dx != -1) {
			return;
		}
		int klick = e.getWheelRotation();
		if (klick == 0) {
			return;
		}
		while (klick > 0 && zoom > 0) {
			klick--;
			zoom(e.getX(), e.getY(), true);
		}
		while (klick < 0 && zoom < 19) {
			klick++;
			zoom(e.getX(), e.getY(), false);
		}

		repaint();
	}

	private void zoom(int xp, int yp, boolean out) {
		double yZ = y + yp / 256f;
		double xZ = x + xp / 256f;
		if (out) {
			yZ /= 2;
			xZ /= 2;
			zoom--;
		} else {
			yZ *= 2;
			xZ *= 2;
			zoom++;
		}
		y = yZ - yp / 256f;
		x = xZ - xp / 256f;
		int limit = 1 << zoom;
		x = (x % limit + limit) % limit;

	}

	@Override
	public void tileFinished(int x, int y, int zoom, BufferedImage tile) {
		repaint();

	}

	@Override
	public void routeModelChanged() {
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!isEnabled()) {
			return;
		}
		if (e.getSource() == in && zoom < 19) {
			zoom(getWidth() / 2, getHeight() / 2, false);
			repaint();
		} else if (e.getSource() == out && zoom > 0) {
			zoom(getWidth() / 2, getHeight() / 2, true);
			repaint();
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		in.setEnabled(enabled);
		out.setEnabled(enabled);
		startCalc.setVisible(!enabled);
		super.setEnabled(enabled);
	}

	public void setMapLocation(Coordinates firstNode, Coordinates lastNode) {
		zoom = 14;
		int width = getWidth();
		if (width == 0) {
			width = 712;
		}
		int height = getHeight();
		if (height == 0) {
			height = 600;
		}
		x = (firstNode.getSmtX(zoom) + lastNode.getSmtX(zoom)) / 2d - width
				/ 512d;
		y = (firstNode.getSmtY(zoom) + lastNode.getSmtY(zoom)) / 2d - height
				/ 512d;
	}
}
