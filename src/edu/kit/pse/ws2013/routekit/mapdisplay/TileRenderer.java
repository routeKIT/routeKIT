package edu.kit.pse.ws2013.routekit.mapdisplay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.map.Graph;
import edu.kit.pse.ws2013.routekit.util.Coordinates;

/**
 * Eine {@link TileSource}, die die Kacheln selbst berechnet.
 */
public class TileRenderer implements TileSource {
	Graph graph;
	final int space = 10;
	int checkValue;

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
		int startNode;
		int targetNode;
		Coordinates coordsTargetNode;
		Coordinates coordsStartNode;
		Coordinates leftTop = Coordinates.fromSmt(x, y + 1, zoom);
		Coordinates rightBottom = Coordinates.fromSmt(x + 1, y, zoom);
		Set<Integer> edges = graph.getIndex(zoom).getEdgesInRectangle(leftTop,
				rightBottom);
		BufferedImage tile = new BufferedImage(256, 256,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = tile.createGraphics();
		g.setColor(Color.white);
		g.fillRect(1, 1, 255, 255);
		for (Integer e : edges) {
			startNode = graph.getStartNode(e);
			coordsStartNode = graph.getCoordinates(startNode);
			targetNode = graph.getTargetNode(e);
			coordsTargetNode = graph.getCoordinates(targetNode);

			int xstart = (int) ((coordsStartNode.getSmtX(zoom) - x) * 256);
			int ystart = (int) ((coordsStartNode.getSmtY(zoom) - y) * 256);
			int xtarget = (int) ((coordsTargetNode.getSmtX(zoom) - x) * 256);
			int ytarget = (int) ((coordsTargetNode.getSmtY(zoom) - y) * 256);

			/*
			 * int xstart = (int) (doublerest(coordsStartNode.getSmtX(zoom)) *
			 * 256); int ystart = (int)
			 * (doublerest(coordsStartNode.getSmtY(zoom)) * 256); int xtarget =
			 * (int) (doublerest(coordsTargetNode.getSmtX(zoom)) * 256); int
			 * ytarget = (int) (doublerest(coordsTargetNode.getSmtY(zoom)) *
			 * 256);
			 */
			g.setColor(Color.black);
			g.drawLine(xstart, ystart, xtarget, ytarget);

			/*
			 * String name = getName(e); checkValue = (int)
			 * Math.ceil(Math.sqrt(Math.pow((xtarget - xstart), 2) +
			 * Math.pow((ytarget - ystart), 2))) + 2 * space; if (name != null
			 * && name.length() <= checkValue) { AffineTransform at =
			 * AffineTransform.getRotateInstance(Math
			 * .toRadians(getAngle(xstart, ystart, xtarget, ytarget)), xstart,
			 * ystart); Graphics2D g2 = tile.createGraphics();
			 * g2.setTransform(at); g2.setFont(new Font(Font.SANS_SERIF, 0,
			 * 10)); g2.drawString(name, space + xstart, ystart); }
			 */

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

	private double getAngle(int xstart, int ystart, int xtarget, int ytarget) {
		if (xstart == xtarget) {
			return 90d;
		}
		if (ystart == ytarget) {
			return 0d;
		}
		if ((ystart > ytarget && xstart < xtarget)
				|| (ystart < ytarget && xtarget < xstart)) {
			return (180 - Math.toDegrees(Math.asin(Math.abs(ytarget - ystart)
					/ checkValue)));
		}
		if ((ystart < ytarget && xstart < xtarget)
				|| (ytarget < ystart && xtarget < xstart)) {
			return Math.toDegrees(Math.asin((Math.abs(ytarget - ystart))
					/ checkValue));
		}
		return -1;
	}

	private float doublerest(float number) {
		int res = (int) number;
		return (number - res);
	}

}
