package edu.kit.pse.ws2013.routekit.mapdisplay;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
	Graph graph;
	private static final int space = 10;

	private static final boolean DO_COLORFULL = true;

	/**
	 * Konstruktor: Erzeugt einen neuen {@code TileRenderer}.
	 * 
	 * @param graph
	 *            Ein Adjazenzfeld.
	 */
	public TileRenderer(Graph graph) {
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
	public BufferedImage renderTile(int x, int y, int zoom) {
		int x1 = -1;
		int y1 = -1;
		int startNode;
		int targetNode;
		Coordinates coordsTargetNode;
		Coordinates coordsStartNode;
		Coordinates leftTop = Coordinates.fromSmt(x - 0.1f, y + 1.1f, zoom);
		Coordinates rightBottom = Coordinates.fromSmt(x + 1.1f, y - 0.1f, zoom);
		Set<Integer> edges = graph.getIndex(zoom).getEdgesInRectangle(leftTop,
				rightBottom);
		BufferedImage tile = new BufferedImage(256, 256,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = tile.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, 256, 256);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Font font = new Font(Font.SANS_SERIF, 0, 12);
		for (Integer e : edges) {
			startNode = graph.getStartNode(e);
			coordsStartNode = graph.getCoordinates(startNode);
			targetNode = graph.getTargetNode(e);
			coordsTargetNode = graph.getCoordinates(targetNode);
			int xstart = (int) ((coordsStartNode.getSmtX(zoom) - x) * 256);
			int ystart = (int) ((coordsStartNode.getSmtY(zoom) - y) * 256);
			int xtarget = (int) ((coordsTargetNode.getSmtX(zoom) - x) * 256);
			int ytarget = (int) ((coordsTargetNode.getSmtY(zoom) - y) * 256);
			EdgeProperties p = graph.getEdgeProperties(e);
			if (DO_COLORFULL) {
				g.setColor(Color.getHSBColor(p.getType().ordinal()
						/ ((float) HighwayType.values().length), 1, 1));
			} else {
				g.setColor(Color.black);
			}
			g.drawLine(xstart, ystart, xtarget, ytarget);

			if (xstart == xtarget) {
				if (ystart < ytarget) {
					x1 = xstart;
					y1 = ystart;
				}
				if (ystart > ytarget) {
					x1 = xtarget;
					y1 = ytarget;
				}
			} else {
				if (xstart < xtarget) {
					x1 = xstart;
					y1 = ystart;
				} else {
					x1 = xtarget;
					y1 = ytarget;
				}
			}

			String name = getName(e);
			double checkValue = Math.sqrt(Math.pow((xtarget - xstart), 2)
					+ Math.pow((ytarget - ystart), 2));
			if (name != null && (x1 != -1)) {
				Rectangle2D r = font.getStringBounds(name,
						g.getFontRenderContext());
				AffineTransform at = AffineTransform.getRotateInstance(
						getAngle(xstart, ystart, xtarget, ytarget, checkValue),
						x1, y1);
				g.setColor(Color.BLUE);
				AffineTransform old = g.getTransform();
				g.setTransform(at);
				g.setFont(font);
				int i = 1;
				String nNames = "";
				while (i * (r.getWidth() + 3) - 3 + 2 * space < checkValue) {
					i++;
					if (!nNames.isEmpty()) {
						nNames += " ";
					}
					nNames += name;
				}
				g.drawString(nNames, space + x1, y1);
				g.setTransform(old);
			}

		}
		return tile;
	}

	private String getName(int edge) {
		String name = graph.getEdgeProperties(edge).getName();
		String number = graph.getEdgeProperties(edge).getRoadRef();
		if (name == null && number == null) {
			return null;
		}
		if (name != null) {
			return name;
		} else {
			return number;
		}
	}

	private double getAngle(int xstart, int ystart, int xtarget, int ytarget,
			double checkValue) {
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
