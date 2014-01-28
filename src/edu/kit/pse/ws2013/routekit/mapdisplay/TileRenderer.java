package edu.kit.pse.ws2013.routekit.mapdisplay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.map.EdgeProperties;
import edu.kit.pse.ws2013.routekit.map.Graph;
import edu.kit.pse.ws2013.routekit.map.HighwayType;
import edu.kit.pse.ws2013.routekit.util.Coordinates;

/**
 * A {@link TileSource} that renders tiles itself.
 */
public class TileRenderer implements TileSource {
	private Graph graph;

	class EdgeIterator {
		private int xstart;
		private int ystart;
		private int xtarget;
		private int ytarget;
		EdgeProperties p;
		int edge;
		Iterator<Integer> edges;
		private int zoom;
		private int x;
		private int y;

		public EdgeIterator(Iterator<Integer> edges, int zoom, int x, int y) {
			this.edges = edges;
			this.zoom = zoom;
			this.x = x;
			this.y = y;
		}

		public boolean next() {
			do {
				if (!edges.hasNext()) {
					return false;
				}

				edge = edges.next();
			} while (graph.getCorrespondingEdge(edge) > edge);
			extractCoordinates();
			return true;
		}

		/**
		 * Sets start and target coordinates
		 */
		private void extractCoordinates() {
			int startNode = graph.getStartNode(edge);
			int targetNode = graph.getTargetNode(edge);
			Coordinates coordsTargetNode = graph.getCoordinates(targetNode);
			Coordinates coordsStartNode = graph.getCoordinates(startNode);
			xstart = (int) ((coordsStartNode.getSmtX(zoom) - x) * 256);
			ystart = (int) ((coordsStartNode.getSmtY(zoom) - y) * 256);
			xtarget = (int) ((coordsTargetNode.getSmtX(zoom) - x) * 256);
			ytarget = (int) ((coordsTargetNode.getSmtY(zoom) - y) * 256);
			p = graph.getEdgeProperties(edge);
		}

	}

	private static final int space = 10;

	private static final boolean DO_COLORFULL = true;

	/**
	 * Creates a new {@link TileRenderer} for the given graph.
	 * 
	 * @param graph
	 *            The {@link Graph} to render.
	 */
	public TileRenderer(final Graph graph) {
		this.graph = graph;
	}

	@Override
	public BufferedImage renderTile(final int x, final int y, final int zoom) {
		final Set<Integer> edges = getEdgesOnTile(x, y, zoom);

		final BufferedImage tile = new BufferedImage(256, 256,
				BufferedImage.TYPE_INT_RGB);
		final Graphics2D g = tile.createGraphics();
		g.setColor(new Color(210, 210, 210));
		g.fillRect(0, 0, 256, 256);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		final Stroke oldStroke = g.getStroke();
		// Draw border
		EdgeIterator it = new EdgeIterator(edges.iterator(), zoom, x, y);
		while (it.next()) {
			g.setColor(getMainStreetColor(it.p.getType(), true));
			g.setStroke(new BasicStroke(getStreetWidth(zoom, it.p) + 4,
					BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.drawLine(it.xstart, it.ystart, it.xtarget, it.ytarget);
		}

		// Draw street
		it = new EdgeIterator(edges.iterator(), zoom, x, y);
		while (it.next()) {
			g.setColor(getMainStreetColor(it.p.getType(), false));
			g.setStroke(new BasicStroke(getStreetWidth(zoom, it.p),
					BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.drawLine(it.xstart, it.ystart, it.xtarget, it.ytarget);
		}

		g.setStroke(oldStroke);

		// Draw name of street – must be above all streets
		final Font font = new Font(Font.SANS_SERIF, 0, 12);
		g.setColor(Color.BLACK);
		g.setFont(font);
		it = new EdgeIterator(edges.iterator(), zoom, x, y);
		while (it.next()) {
			final String name = getName(it.edge);
			final double streetLength = Math.sqrt(Math.pow(
					(it.xtarget - it.xstart), 2)
					+ Math.pow((it.ytarget - it.ystart), 2));
			if (name != null && (it.xstart != -1)
					&& getStreetWidth(zoom, it.p) > 7) {
				final AffineTransform old = g.getTransform();

				final Rectangle2D r = font.getStringBounds(name,
						g.getFontRenderContext());
				double angle = getAngle(it.xstart, it.ystart, it.xtarget,
						it.ytarget, streetLength);

				// Pfeil <-
				if (zoom > 15 && streetLength > 40) {
					if (graph.getCorrespondingEdge(it.edge) == -1) {
						g.setColor(Color.WHITE);
						double angle2 = angle;
						if (it.xtarget < it.xstart
								|| (it.xstart == it.xtarget && it.ystart > it.ytarget)) {
							angle2 -= Math.PI;
						}
						AffineTransform rotateInstance = AffineTransform
								.getRotateInstance(angle2, it.xstart, it.ystart);
						rotateInstance.translate(15 + it.xstart, it.ystart);
						g.setTransform(rotateInstance);
						drawArrow(zoom, g);
						g.setColor(Color.BLACK);

					}
				}

				int xstart;
				int ystart;
				if (it.xstart < it.xtarget
						|| (it.xstart == it.xtarget && it.ystart < it.ytarget)) {
					xstart = it.xstart;
					ystart = it.ystart;
				} else {
					xstart = it.xtarget;
					ystart = it.ytarget;
				}
				final AffineTransform at = AffineTransform.getRotateInstance(
						angle, xstart, ystart);
				g.setTransform(at);
				if (r.getWidth() + 5 * space < streetLength) {
					g.drawString(name, (int) (xstart + streetLength / 2 - r
							.getWidth() / 2),
							(int) (ystart - r.getY() / 2 - r.getY() - r
									.getHeight()) + 1);
				}
				g.setTransform(old);

			}
		}
		return tile;
	}

	private void drawArrow(final int zoom, final Graphics2D g) {
		BasicStroke pen = new BasicStroke(2F, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_MITER);
		Polygon p = new Polygon();
		double peakLength = 0.4;
		double tailThickness = 0.1;

		double length = 10 + (zoom - 15) * 10 / 4;
		double height = 4 + (zoom - 15) * 3 / 4;

		int ycenter = -(int) height / 2;

		p.addPoint((int) (length * peakLength),
				(int) (ycenter + (height - (height * tailThickness)) / 2));
		p.addPoint((int) (length),
				(int) (ycenter + (height - (height * tailThickness)) / 2));

		p.addPoint((int) (length), ycenter);
		p.addPoint((int) ((length) + (length * peakLength)), (int) height / 2
				+ ycenter);
		p.addPoint((int) (length), (int) height + ycenter);

		p.addPoint(
				(int) (length),
				(int) (ycenter + (height - (height * tailThickness)) / 2 + (height * tailThickness)));
		p.addPoint(
				(int) (length * peakLength),
				(int) (ycenter + (height - (height * tailThickness)) / 2 + (height * tailThickness)));

		g.setStroke(pen);
		g.fillPolygon(p);
		g.drawPolygon(p);
	}

	private Set<Integer> getEdgesOnTile(final int x, final int y, final int zoom) {
		final Coordinates leftTop = Coordinates.fromSmt(x - 0.1f, y + 1.1f,
				zoom);
		final Coordinates rightBottom = Coordinates.fromSmt(x + 1.1f, y - 0.1f,
				zoom);
		final Set<Integer> edges = graph.getIndex(zoom).getEdgesInRectangle(
				leftTop, rightBottom);
		return edges;
	}

	private Color getMainStreetColor(HighwayType type, boolean border) {
		Color col;
		if (DO_COLORFULL) {
			col = Color.getHSBColor(
					type.ordinal() / ((float) HighwayType.values().length),
					0.2f, border ? 0.7f : 1f);
		} else {
			col = Color.BLACK;
		}
		return col;
	}

	private float getStreetWidth(int zoom, EdgeProperties p) {
		float width;
		if (p.getType() != HighwayType.Tertiary
				&& p.getType() != HighwayType.Unclassified
				&& p.getType() != HighwayType.Residential) {
			width = (HighwayType.values().length - p.getType().ordinal()) * 6
					/ (20f - zoom) + 2;
		} else {
			width = Math.max(28 - (20 - zoom) * 5, 1);
		}
		return width;
	}

	private String getName(final int edge) {
		final String name = graph.getEdgeProperties(edge).getName();
		final String number = graph.getEdgeProperties(edge).getRoadRef();
		if (name == null && number == null) {
			return null;
		}
		if (name != null) {
			return name;
		} else {
			return number;
		}
	}

	private double getAngle(final int xstart, final int ystart,
			final int xtarget, final int ytarget, final double checkValue) {
		if (xstart == xtarget) {
			return (Math.PI / 2d);
		}
		if (ystart == ytarget) {
			return 0d;
		}
		if ((ystart > ytarget && xstart < xtarget)
				|| (ystart < ytarget && xtarget < xstart)) {
			return (-Math.asin((Math.abs(ytarget - ystart) / checkValue)));
		}
		if ((ystart < ytarget && xstart < xtarget)
				|| (ytarget < ystart && xtarget < xstart)) {
			return Math.asin((Math.abs(ytarget - ystart)) / checkValue);
		}
		return -1;
	}
}
