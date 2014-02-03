package edu.kit.pse.ws2013.routekit.map;

import java.io.File;
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

import javax.swing.JOptionPane;

import edu.kit.pse.ws2013.routekit.models.ProgressReporter;
import edu.kit.pse.ws2013.routekit.util.Coordinates;

/**
 * A street map graph.
 * 
 * <p>
 * Note that this graph data structure is profile-independent and, like
 * {@link EdgeBasedGraph}, is created when a new map is imported.
 * 
 * @see StreetMap
 */
public class Graph {
	int[] nodes;
	int[] edges;
	int[] edgesReverse;
	Map<Integer, NodeProperties> nodeProps;
	EdgeProperties[] edgeProps;
	float[] lat;
	float[] lon;
	GraphIndex[] indices;
	int[] correspondingEdges;

	/**
	 * Creates a new {@code Graph} from the given adjacency field.
	 * 
	 * @param nodes
	 *            the node array of the adjacency field
	 * @param edges
	 *            the edge array of the adjacency field
	 * @param nodeProps
	 *            the properties of the nodes (A {@code Map} of
	 *            {@code NodeProperties} is used here as most nodes do not have
	 *            any special properties and an array would be empty for the
	 *            most part.)
	 * @param edgeProps
	 *            the properties of the edges
	 * @param lat
	 *            the latitudes of the nodes
	 * @param lon
	 *            the longitudes of the nodes
	 * @throws IllegalArgumentException
	 *             if {@code edgeProps} does not have as many elements as
	 *             {@code edges}
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
		correspondingEdges = new int[edges.length];
		for (int i = 0; i < edges.length; i++) {
			int target = edges[i];
			int limit = (target == nodes.length - 1 ? edges.length
					: nodes[target + 1]);
			correspondingEdges[i] = -1;
			for (int j = nodes[target]; j < limit; j++) {
				if (getTargetNode(j) == getStartNode(i)) {
					correspondingEdges[i] = j;
					break;
				}
			}
		}
		initIndices();
	}

	private void initIndices() {
		Thread[] threads = new Thread[3];
		indices = new GraphIndex[3];
		threads[0] = new Thread("L2 Graph-Index") {
			@Override
			public void run() {
				indices[0] = new GraphIndex(Graph.this,
						HighwayType.Residential, new ReducedGraphView(
								Graph.this, HighwayType.Primary));
			}
		};
		threads[1] = new Thread("L1 Graph-Index") {
			@Override
			public void run() {
				indices[1] = new GraphIndex(Graph.this, HighwayType.Tertiary,
						new IdentityGraphView(Graph.this));
			}
		};
		threads[2] = new Thread("L0 Graph-Index") {
			@Override
			public void run() {
				indices[2] = new GraphIndex(Graph.this,
						HighwayType.Residential, new IdentityGraphView(
								Graph.this));
			}
		};
		for (Thread t : threads) {
			t.start();
		}
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// check if we got all the indices
		for (int i = 0; i < indices.length; i++) {
			if (indices[i] == null) {
				// this thread crashed (e. g. OOM), let’s try again
				// this time, synchronously – only one index at a time
				try {
					threads[i].run();
				} catch (OutOfMemoryError e) {
					JOptionPane.showMessageDialog(null,
							"Nicht genug Arbeitsspeicher!");
					System.exit(1);
				}
			}
		}
	}

	/**
	 * Returns the {@code NodeProperties} of the given node.
	 * 
	 * @param node
	 *            a node
	 * @return the properties of the node, or {@code null} if none
	 */
	public NodeProperties getNodeProperties(int node) {
		return nodeProps.get(node);
	}

	/**
	 * Returns the coordinates of the given node.
	 * 
	 * @param node
	 *            a node
	 * @return the {@link Coordinates} of the node
	 */
	public Coordinates getCoordinates(int node) {
		return new Coordinates(lat[node], lon[node]);
	}

	/**
	 * Returns the start node of the given edge.
	 * 
	 * @param edge
	 *            an edge
	 * @return the start node of the edge
	 */
	public int getStartNode(int edge) {
		return edgesReverse[edge];
	}

	/**
	 * Returns a set of all outgoing edges of the given node.
	 * 
	 * @param node
	 *            a node
	 * @return all edges going out from this node
	 */
	public Set<Integer> getOutgoingEdges(int node) {
		return new IntIntervalSet(nodes[node],
				(node == nodes.length - 1 ? edges.length : nodes[node + 1])
						- nodes[node]);
	}

	/**
	 * Returns a geometric data structure for fast edge search in the given zoom
	 * level.
	 * 
	 * @param zoom
	 *            the zoom level
	 * @return the {@link GraphIndex} data structure
	 */
	public GraphIndex getIndex(int zoom) {
		if (zoom > 13) {
			return indices[2];
		}
		if (zoom > 10) {
			return indices[1];
		}
		return indices[0];
	}

	/**
	 * Returns a set of all incoming edges of the given node.
	 * 
	 * @param node
	 *            a node
	 * @return all edges coming in at that node
	 */
	public Set<Integer> getIncomingEdges(int node) {
		throw new Error("Unimplemented");
	}

	/**
	 * Returns the edge in the opposite direction, or -1 if it doesn't exist.
	 * 
	 * @param edge
	 *            to search for
	 * @return the corresponding edge
	 */
	public int getCorrespondingEdge(int edge) {
		return correspondingEdges[edge];
	}

	/**
	 * Returns the {@link EdgeProperties} of the given edge.
	 * 
	 * @param edge
	 *            an edge
	 * @return the properties of the edge
	 */
	public EdgeProperties getEdgeProperties(int edge) {
		return edgeProps[edge];
	}

	/**
	 * Returns the end node of the given edge.
	 * 
	 * @param edge
	 *            an edge
	 * @return the end node of the edge
	 */
	public int getTargetNode(int edge) {
		return edges[edge];
	}

	/**
	 * Returns the number of edges in this graph.
	 * 
	 * @return the number of edges
	 */
	public int getNumberOfEdges() {
		return edges.length;
	}

	/**
	 * Returns the number of nodes in this graph.
	 * 
	 * @return the number of nodes
	 */
	public int getNumberOfNodes() {
		return nodes.length;
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
			final int headerLength = 2 + "routeKIT".length() + 3 + 8;
			assert (raf.getFilePointer() == headerLength);
			final int intsLength = nNodes * 4 + nEdges * 4;
			final int floatsLength = nNodes * 4 + nNodes * 4;
			final int dataLength = intsLength + floatsLength;
			MappedByteBuffer b = raf.getChannel().map(MapMode.READ_WRITE,
					headerLength, dataLength);
			IntBuffer ints = b.asIntBuffer();
			ints.put(nodes, 0, nNodes);
			ints.put(edges, 0, nEdges);
			b.position(intsLength);
			FloatBuffer floats = b.asFloatBuffer();
			floats.put(lat, 0, nNodes);
			floats.put(lon, 0, nNodes);

			raf.seek(headerLength + dataLength);

			for (Entry<Integer, NodeProperties> entry : nodeProps.entrySet()) {
				raf.writeInt(entry.getKey());
				entry.getValue().save(raf);
			}
			raf.writeInt(-1);

			Map<EdgeProperties, Set<Integer>> reverseEdgeProperties = new HashMap<>();
			for (int i = 0; i < nEdges; i++) {
				EdgeProperties props = edgeProps[i];
				Set<Integer> current = reverseEdgeProperties.get(props);
				if (current == null) {
					current = new HashSet<>();
				}
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
	 * @param reporter
	 * @return A {@link Graph} loaded from {@code file}.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public static Graph load(File file, ProgressReporter reporter)
			throws IOException {
		reporter.setSubTasks(new float[] { 0.7f, 0.3f });
		try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
			reporter.pushTask("Lese Datei");
			if (!raf.readUTF().equals("routeKIT")) {
				throw new IOException("Wrong magic!");
			}
			if (raf.readByte() != 0x67) { // 'g', for "graph"
				throw new IOException("Wrong file type!");
			}
			if (raf.readByte() != 1) { // version 1
				throw new IOException("Unsupported version!");
			}
			if (raf.readByte() != 0x04) { // End of Transmission
				throw new IOException("Wrong magic!");
			}

			int nNodes = raf.readInt();
			int nEdges = raf.readInt();
			final int headerLength = 2 + "routeKIT".length() + 3 + 8;
			assert (raf.getFilePointer() == headerLength);
			final int dataLength = nNodes * 4 + nEdges * 4 + nNodes * 4
					+ nNodes * 4;
			int[] nodes = new int[nNodes];
			int[] edges = new int[nEdges];
			float[] lats = new float[nNodes];
			float[] lons = new float[nNodes];
			MappedByteBuffer b = raf.getChannel().map(MapMode.READ_ONLY,
					2 + "routeKIT".length() + 3 + 8,
					nNodes * 4 + nEdges * 4 + nNodes * 4 + nNodes * 4);
			IntBuffer ints = b.asIntBuffer();
			ints.get(nodes, 0, nNodes);
			ints.get(edges, 0, nEdges);
			b.position(b.position() + 4 * nNodes + 4 * nEdges);
			FloatBuffer floats = b.asFloatBuffer();
			floats.get(lats, 0, nNodes);
			floats.get(lons, 0, nNodes);

			raf.seek(headerLength + dataLength);

			Map<Integer, NodeProperties> nodeProps = new HashMap<>();
			int node;
			while ((node = raf.readInt()) != -1) {
				NodeProperties props = NodeProperties.load(raf);
				nodeProps.put(node, props);
			}

			EdgeProperties[] edgeProps = new EdgeProperties[nEdges];
			int length;
			int totalRead = 0;
			while ((length = raf.readInt()) != 0) {
				EdgeProperties props = EdgeProperties.load(raf);
				totalRead += length;
				reporter.setProgress(((float) totalRead) / nEdges);
				while (length-- > 0) {
					edgeProps[raf.readInt()] = props;
				}
			}
			reporter.popTask("Lese Datei");
			reporter.pushTask("Baue Graphstruktur");

			Graph graph = new Graph(nodes, edges, nodeProps, edgeProps, lats,
					lons);
			reporter.popTask();
			return graph;
		}
	}
}
