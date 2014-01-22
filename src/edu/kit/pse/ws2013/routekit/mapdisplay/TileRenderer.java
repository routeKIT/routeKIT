package edu.kit.pse.ws2013.routekit.mapdisplay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
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
 * Eine {@link TileSource}, die die Kacheln selbst berechnet.
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
			if (!edges.hasNext()) {
				return false;
			}
			edge = edges.next();
			extractCoordinates();
			return true;
		}

		// Setzt die Start/Ziel Coordinaten richtig
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

			if ((xstart == xtarget && ystart > ytarget) || xstart > xtarget) {
				int tmp = xstart;
				xstart = xtarget;
				xtarget = tmp;
				tmp = ystart;
				ystart = ytarget;
				ytarget = tmp;
			}
		}

	}

	private static final int space = 10;

	private static final boolean DO_COLORFULL = true;

	/**
	 * Konstruktor: Erzeugt einen neuen {@code TileRenderer}.
	 * 
	 * @param graph
	 *            Ein Adjazenzfeld.
	 */
	public TileRenderer(final Graph graph) {
		this.graph = graph;
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
	@Override
	public BufferedImage renderTile(final int x, final int y, final int zoom) {
		final Set<Integer> edges = getEdgesOnTile(x, y, zoom);

		final BufferedImage tile = new BufferedImage(256, 256,
				BufferedImage.TYPE_INT_RGB);
		final Graphics2D g = tile.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, 256, 256);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		final Stroke oldStroke = g.getStroke();
		// Rand malen
		EdgeIterator it = new EdgeIterator(edges.iterator(), zoom, x, y);
		while (it.next()) {
			g.setColor(getMainStreetColor(it.p.getType(), true));
			g.setStroke(new BasicStroke(getStreetWidth(zoom, it.p) + 4,
					BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.drawLine(it.xstart, it.ystart, it.xtarget, it.ytarget);
		}

		// Straße malen
		it = new EdgeIterator(edges.iterator(), zoom, x, y);
		while (it.next()) {
			g.setColor(getMainStreetColor(it.p.getType(), false));
			g.setStroke(new BasicStroke(getStreetWidth(zoom, it.p),
					BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.drawLine(it.xstart, it.ystart, it.xtarget, it.ytarget);
		}//

		g.setStroke(oldStroke);

		// Name der Straße muss über allen Straßen sein
		final Font font = new Font(Font.SANS_SERIF, 0, 12);
		g.setColor(Color.BLACK);
		g.setFont(font);
		it = new EdgeIterator(edges.iterator(), zoom, x, y);
		while (it.next()) {
			final String name = getName(it.edge);
			final double streetLength = Math.sqrt(Math.pow(
					(it.xtarget - it.xstart), 2)
					+ Math.pow((it.ytarget - it.ystart), 2));
			if (name != null && (it.xstart != -1)) {
				final Rectangle2D r = font.getStringBounds(name,
						g.getFontRenderContext());
				final AffineTransform at = AffineTransform.getRotateInstance(
						getAngle(it.xstart, it.ystart, it.xtarget, it.ytarget,
								streetLength), it.xstart, it.ystart);
				final AffineTransform old = g.getTransform();
				g.setTransform(at);
				int i = 1;
				final StringBuilder nNames = new StringBuilder();
				while (i * (r.getWidth() + 3) - 3 + 2 * space < streetLength) {
					i++;
					if (nNames.length() != 0) {
						nNames.append(' ');
					}
					nNames.append(name);
				}

				g.drawString(nNames.toString(), space + it.xstart,
						(int) (it.ystart - r.getY() / 2 - r.getY() - r
								.getHeight()));
				g.setTransform(old);
			}
		}
		return tile;
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
					type.ordinal() / ((float) HighwayType.values().length), 1,
					border ? 0.5f : 1);
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
					/ (20f - zoom);
		} else {
			width = (HighwayType.values().length - HighwayType.Secondary
					.ordinal()) * 6 / (20f - zoom);
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
