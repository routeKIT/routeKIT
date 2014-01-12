package edu.kit.pse.ws2013.routekit.map;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.util.Coordinates;

/**
 * Ein Kartengraph/Straßennetz. Beachte: Dieser Graph ist nicht das Ergebnis
 * einer Vorberechnung für ein Profil und eine Karte, sondern nur für eine
 * Karte.
 */
public class Graph {
	int[] nodes;
	int[] edges;
	int[] edgesReverse;
	Map<Integer, NodeProperties> nodeProps;
	EdgeProperties[] edgeProps;
	float[] lat;
	float[] lon;

	/**
	 * Konstruktor: Erzeugt ein neues Graph-Objekt aus dem gegebenen
	 * Adjazenzfeld.
	 * 
	 * @param nodes
	 *            Der Knoten-Bestandteil des Adjazenzfeldes.
	 * @param edges
	 *            Der Kanten-Bestandteil des Adjazenzfeldes.
	 * @param nodeProps
	 *            Die {@code NodeProperties} der Knoten des Graphen. Es wird
	 *            eine {@code Map} anstelle eines Arrays verwendet, da die
	 *            meisten Knoten keine besonderen Eigenschaften haben und daher
	 *            das Array zum großen Teil leer wäre.
	 * 
	 * @param edgeProps
	 *            Die {@code EdgeProperties} der Kanten des Graphen. Hier wird
	 *            ein Array verwendet, da jede Kante einen Namen und damit ein
	 *            {@code EdgeProperties}-Objekt hat.
	 * 
	 * @param lat
	 *            Die geographischen Breiten der Knoten des Graphen.
	 * @param lon
	 *            Die geographischen Längen der Knoten des Graphen.
	 */
	public Graph(int[] nodes, int[] edges,
			Map<Integer, NodeProperties> nodeProps, EdgeProperties[] edgeProps,
			float[] lat, float[] lon) {
		if (edgeProps.length != edges.length) {
			throw new IllegalArgumentException(
					"Must have as many EdgeProperties as Edges!");
		}
		this.nodes = nodes;
		this.edges = edges;
		this.nodeProps = nodeProps;
		this.edgeProps = edgeProps;
		this.lat = lat;
		this.lon = lon;
		this.edgesReverse = new int[edges.length];
		int currentNode = 0;
		for (int i = 0; i < edges.length; i++) {
			while (currentNode + 1 < nodes.length
					&& nodes[currentNode + 1] <= i) {
				currentNode++;

			}
			edgesReverse[i] = currentNode;
		}
	}

	/**
	 * Gibt die {@code NodeProperties} des angegebenen Knotens zurück.
	 * 
	 * @param node
	 *            Der Knoten, dessen {@link NodeProperties} gesucht werden.
	 * @return
	 */
	public NodeProperties getNodeProperties(int node) {
		return nodeProps.get(node);
	}

	/**
	 * Gibt die Koordinaten des angegebenen Knotens zurück.
	 * 
	 * @param node
	 *            Der Knoten, dessen Koordinaten gesucht werden.
	 * @return
	 */
	public Coordinates getCoordinates(int node) {
		return new Coordinates(lat[node], lon[node]);
	}

	/**
	 * Gibt den Startknoten der angegebenen Kante zurück.
	 * 
	 * @param edge
	 *            Die Kante, dessen Startknoten gesucht wird.
	 * @return
	 */
	public int getStartNode(int edge) {
		return edgesReverse[edge];
	}

	/**
	 * Gibt alle ausgehenden Kanten des angegebenen Knotens zurück.
	 * 
	 * @param node
	 *            Der Knoten, dessen ausgehende Kanten gesucht werden.
	 * @return
	 */
	public Set<Integer> getOutgoingEdges(int node) {
		return new IntArraySet(nodes[node],
				(node == nodes.length - 1 ? edges.length : nodes[node + 1])
						- nodes[node], edges);
	}

	/**
	 * Gibt eine geometrische Datenstruktur zur angegebenen Zoomstufe zurück.
	 * 
	 * @param zoom
	 *            Die Zoomstufe.
	 * @return
	 */
	public GraphIndex getIndex(int zoom) {
		return new GraphIndex(this, zoom);
	}

	/**
	 * Gibt alle in den Knoten eingehende Kanten zurück.
	 * 
	 * @param node
	 *            Der Knoten, dessen eingehende Kanten gesucht werden.
	 * @return
	 */
	public Set<Integer> getIncomingEdges(int node) {
		return null;
	}

	/**
	 * Gibt die {@code EdgeProperties} der angegebenen Kante zurück.
	 * 
	 * @param edge
	 *            Die Kante, deren {@link EdgeProperties} gesucht werden.
	 * @return
	 */
	public EdgeProperties getEdgeProperties(int edge) {
		return edgeProps[edge];
	}

	/**
	 * Gibt den Endknoten der angegebenen Kante zurück.
	 * 
	 * @param edge
	 *            Die Kante, dessen Endknoten gesucht wird.
	 * @return
	 */
	public int getTargetNode(int edge) {
		return edges[edge];
	}

	public int getEdgesCount() {
		return edges.length;
	}

	/**
	 * Save this graph to the specified file.
	 * <p>
	 * The only thing you need to know about the format of the file is that
	 * {@link #load(File)} can parse it; the rest of this documentation is
	 * purely informational, as the format may change in the future (but we’ll
	 * try and maintain backwards compatibility).
	 * <h4>The file format</h4>
	 * <h5>Header</h5>
	 * <ul>
	 * <li>Magic: bytes {@code 0x00}, {@code 0x02}, ASCII {@code routeKIT}</li>
	 * <li>ASCII {@code g} (for "graph")</li>
	 * <li>byte {@code 0x01} (for version 1, but future versions won’t
	 * necessarily use the same header format)</li>
	 * <li>ASCII {@code End of Transmission}</li>
	 * </ul>
	 * <h5>Nodes, Edges</h5>
	 * <ul>
	 * <li>int number of nodes, int number of edges (both four bytes, high byte
	 * first)</li>
	 * <li>ints nodes (all four bytes, high byte first)</li>
	 * <li>ints edges (all four bytes, high byte first)</li>
	 * <li>floats latitudes (all four bytes, IEEE 754 single)</li>
	 * <li>floats longitudes (all four bytes, IEEE 754 single)</li>
	 * </ul>
	 * <h5>NodeProperties, EdgeProperties</h5>
	 * <ul>
	 * <li>For each {@link NodeProperties}, int node (four bytes, high byte
	 * first), then the {@link NodeProperties} (see
	 * {@link NodeProperties#save(java.io.DataOutput)})</li>
	 * <li>int {@code -1} (four bytes, high byte first)</li>
	 * <li>For each {@link EdgeProperties}, int number of edges with these
	 * properties (four bytes, high byte first), then the {@link EdgeProperties}
	 * (see {@link EdgeProperties#save(java.io.DataOutput)}), then ints edges
	 * (all four bytes, high byte first)</li>
	 * <li>int {@code 0} (four bytes, high byte first)</li>
	 * </ul>
	 * 
	 * @param file
	 *            The file.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void save(File file) throws IOException {
		try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {

			raf.writeUTF("routeKIT");
			raf.writeByte(0x67); // 'g', for "graph"
			raf.writeByte(1); // version 1
			raf.writeByte(0x04); // End of Transmission

			int nNodes = nodes.length;
			int nEdges = edges.length;
			raf.writeInt(nNodes);
			raf.writeInt(nEdges);
			MappedByteBuffer b = raf.getChannel().map(MapMode.READ_WRITE,
					2 + "routeKIT".length() + 3 + 8,
					nNodes * 4 + nEdges * 4 + nNodes * 4 + nNodes * 4);
			IntBuffer ints = b.asIntBuffer();
			ints.put(nodes, 0, nNodes);
			ints.put(edges, 0, nEdges);
			b.position(b.position() + 4 * nNodes + 4 * nEdges);
			FloatBuffer floats = b.asFloatBuffer();
			floats.put(lat, 0, nNodes);
			floats.put(lon, 0, nNodes);

			int toSkip = nNodes * 4 + nEdges * 4 + nNodes * 4 + nNodes * 4;
			while (toSkip > 0)
				toSkip -= raf.skipBytes(toSkip);

			for (Entry<Integer, NodeProperties> entry : nodeProps.entrySet()) {
				raf.writeInt(entry.getKey());
				entry.getValue().save(raf);
			}
			raf.writeInt(-1);

			Map<EdgeProperties, Set<Integer>> reverseEdgeProperties = new HashMap<>();
			for (int i = 0; i < nEdges; i++) {
				EdgeProperties props = edgeProps[i];
				Set<Integer> current = reverseEdgeProperties.get(props);
				if (current == null)
					current = new HashSet<>();
				current.add(i);
				reverseEdgeProperties.put(props, current);
			}
			for (Entry<EdgeProperties, Set<Integer>> entry : reverseEdgeProperties
					.entrySet()) {
				raf.writeInt(entry.getValue().size());
				entry.getKey().save(raf);
				for (int i : entry.getValue()) {
					raf.writeInt(i);
				}
			}
			raf.writeInt(0);
		}
	}

	/**
	 * Loads a graph from the specified file.
	 * <p>
	 * This method can parse files generated by the current version of
	 * {@link #save(File)}, and it will also attempt to parse files generated by
	 * earlier versions of that method. For the file format, see there.
	 * 
	 * @param file
	 *            The file.
	 * @return A {@link Graph} loaded from {@code file}.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public static Graph load(File file) throws IOException {
		try (FileInputStream fis = new FileInputStream(file);
				DataInputStream dis = new DataInputStream(fis)) {
			if (!dis.readUTF().equals("routeKIT")) {
				throw new IOException("Wrong magic!");
			}
			if (dis.readByte() != 0x67) { // 'g', for "graph"
				throw new IOException("Wrong file type!");
			}
			if (dis.readByte() != 1) { // version 1
				throw new IOException("Unsupported version!");
			}
			if (dis.readByte() != 0x04) { // End of Transmission
				throw new IOException("Wrong magic!");
			}

			int nNodes = dis.readInt();
			int nEdges = dis.readInt();
			int[] nodes = new int[nNodes];
			int[] edges = new int[nEdges];
			float[] lats = new float[nNodes];
			float[] lons = new float[nNodes];
			MappedByteBuffer b = fis.getChannel().map(MapMode.READ_ONLY,
					2 + "routeKIT".length() + 3 + 8,
					nNodes * 4 + nEdges * 4 + nNodes * 4 + nNodes * 4);
			IntBuffer ints = b.asIntBuffer();
			ints.get(nodes, 0, nNodes);
			ints.get(edges, 0, nEdges);
			b.position(b.position() + 4 * nNodes + 4 * nEdges);
			FloatBuffer floats = b.asFloatBuffer();
			floats.get(lats, 0, nNodes);
			floats.get(lons, 0, nNodes);

			int toSkip = nNodes * 4 + nEdges * 4 + nNodes * 4 + nNodes * 4;
			while (toSkip > 0)
				toSkip -= dis.skipBytes(toSkip);

			Map<Integer, NodeProperties> nodeProps = new HashMap<>();
			int node;
			while ((node = dis.readInt()) != -1) {
				NodeProperties props = NodeProperties.load(dis);
				nodeProps.put(node, props);
			}

			EdgeProperties[] edgeProps = new EdgeProperties[nEdges];
			int length;
			while ((length = dis.readInt()) != 0) {
				EdgeProperties props = EdgeProperties.load(dis);
				while (length-- > 0) {
					edgeProps[dis.readInt()] = props;
				}
			}

			return new Graph(nodes, edges, nodeProps, edgeProps, lats, lons);
		}
	}
}
