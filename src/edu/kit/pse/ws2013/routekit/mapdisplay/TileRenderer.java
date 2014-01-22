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
	private int xstart;
	private int ystart;
	private int xtarget;
	private int ytarget;
	int startNode;
	int targetNode;
	Coordinates coordsTargetNode;
	Coordinates coordsStartNode;
	EdgeProperties p;
	float width;
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
		final Coordinates leftTop = Coordinates.fromSmt(x - 0.1f, y + 1.1f,
				zoom);
		final Coordinates rightBottom = Coordinates.fromSmt(x + 1.1f, y - 0.1f,
				zoom);
		final Set<Integer> edges = graph.getIndex(zoom).getEdgesInRectangle(
				leftTop, rightBottom);
		final BufferedImage tile = new BufferedImage(256, 256,
				BufferedImage.TYPE_INT_RGB);
		final Graphics2D g = tile.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, 256, 256);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		final Font font = new Font(Font.SANS_SERIF, 0, 12);
		// Rand malen
		for (final Integer e : edges) {
			setCoordinates(e, zoom, x, y);
			switch (p.getType()) {
			case Motorway:
				g.setColor(new Color(223, 26, 61));
				break;
			case Trunk:
				g.setColor(new Color(248, 168, 3));
				break;
			case Primary:
				g.setColor(new Color(44, 201, 16));
				break;
			case Secondary:
				g.setColor(new Color(39, 177, 133));
				break;
			case Tertiary:
				g.setColor(new Color(19, 52, 140));
				break;
			case Unclassified:
				g.setColor(new Color(16, 43, 112));
				break;
			case Residential:
				g.setColor(new Color(113, 15, 105));
				break;
			default:
				break;
			}
			g.setStroke(new BasicStroke(width + 4, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND));
			g.drawLine(xstart, ystart, xtarget, ytarget);
		}

		// Straße malen
		for (final Integer e : edges) {
			setCoordinates(e, zoom, x, y);
			if (DO_COLORFULL) {
				g.setColor(Color.getHSBColor(p.getType().ordinal()
						/ ((float) HighwayType.values().length), 1, 1));
			} else {
				g.setColor(Color.BLACK);
			}
			final Stroke oldStroke = g.getStroke();
			g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND));
			g.drawLine(xstart, ystart, xtarget, ytarget);
			g.setStroke(oldStroke);
		}
		// Name der Straße muss über allen Straßen sein
		for (final Integer e : edges) {
			setCoordinates(e, zoom, x, y);
			final String name = getName(e);
			final double checkValue = Math.sqrt(Math.pow((xtarget - xstart), 2)
					+ Math.pow((ytarget - ystart), 2));
			if (name != null && (xstart != -1)) {
				final Rectangle2D r = font.getStringBounds(name,
						g.getFontRenderContext());
				final AffineTransform at = AffineTransform.getRotateInstance(
						getAngle(xstart, ystart, xtarget, ytarget, checkValue),
						xstart, ystart);
				g.setColor(Color.BLACK);
				final AffineTransform old = g.getTransform();
				g.setTransform(at);
				g.setFont(font);
				int i = 1;
				final StringBuilder nNames = new StringBuilder();
				while (i * (r.getWidth() + 3) - 3 + 2 * space < checkValue) {
					i++;
					if (nNames.length() != 0) {
						nNames.append(' ');
					}
					nNames.append(name);
				}
				if (zoom == 19) {
					g.drawString(nNames.toString(), space + xstart, ystart
							+ (int) (width / 4f));
				} else {
					g.drawString(nNames.toString(), space + xstart, ystart
							+ (int) (width / 2f));
				}
				g.setTransform(old);
			}
		}
		return tile;
	}

	// Setzt die Start/Ziel Coordinaten richtig
	private void setCoordinates(int edge, int zoom, int x, int y) {
		startNode = graph.getStartNode(edge);
		coordsStartNode = graph.getCoordinates(startNode);
		targetNode = graph.getTargetNode(edge);
		coordsTargetNode = graph.getCoordinates(targetNode);
		xstart = (int) ((coordsStartNode.getSmtX(zoom) - x) * 256);
		ystart = (int) ((coordsStartNode.getSmtY(zoom) - y) * 256);
		xtarget = (int) ((coordsTargetNode.getSmtX(zoom) - x) * 256);
		ytarget = (int) ((coordsTargetNode.getSmtY(zoom) - y) * 256);
		p = graph.getEdgeProperties(edge);
		if (p.getType() != HighwayType.Tertiary
				&& p.getType() != HighwayType.Unclassified
				&& p.getType() != HighwayType.Residential) {
			width = (HighwayType.values().length - p.getType().ordinal()) * 6
					/ (20f - zoom);
		} else {
			width = (HighwayType.values().length - HighwayType.Secondary
					.ordinal()) * 6 / (20f - zoom);
		}

		if ((xstart == xtarget && ystart > ytarget) || xstart > xtarget) {
			int tmp = xstart;
			xstart = xtarget;
			xtarget = tmp;
			tmp = ystart;
			ystart = ytarget;
			ytarget = tmp;
		}
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
