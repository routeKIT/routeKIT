package edu.kit.pse.ws2013.routekit.mapdisplay;

import java.awt.image.BufferedImage;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.map.Graph;
import edu.kit.pse.ws2013.routekit.util.Coordinates;

/**
 * Eine {@link TileSource}, die die Kacheln selbst berechnet.
 */
public class TileRenderer implements TileSource {
	Graph graph;

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
		Coordinates leftTop = Coordinates.fromSmt(x, y, zoom);
		Coordinates rightBottom = Coordinates.fromSmt(x + 1, y + 1, zoom);
		Set<Integer> edges = graph.getIndex(zoom).getEdgesInRectangle(leftTop,
				rightBottom);
		for (Integer e : edges) {
			startNode = graph.getStartNode(e);
			coordsStartNode = graph.getCoordinates(startNode);
			targetNode = graph.getTargetNode(e);
			coordsTargetNode = graph.getCoordinates(targetNode);
		}
		return null;
	}
}
