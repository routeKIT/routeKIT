package edu.kit.pse.ws2013.routekit.map;

import java.io.File;
import java.io.IOException;

/**
 * A street map.
 */
public class StreetMap {
	protected String name;
	protected Graph graph;
	protected EdgeBasedGraph edgeBasedGraph;

	/**
	 * Determines if this is a default map.
	 * 
	 * @return {@code true} if the map is a default map, else {@code false}.
	 */
	public boolean isDefault() {
		return false;
	}

	/**
	 * Creates a new map object from the given graphs.
	 * 
	 * @param graph
	 *            The map graph.
	 * @param edgeBasedGraph
	 *            The edge-based graph.
	 */
	public StreetMap(Graph graph, EdgeBasedGraph edgeBasedGraph) {
		this.graph = graph;
		this.edgeBasedGraph = edgeBasedGraph;
	}

	public EdgeBasedGraph getEdgeBasedGraph() {
		return edgeBasedGraph;
	}

	public Graph getGraph() {
		return graph;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name; // TODO do we need this? I don’t think so – Lucas
	}

	/**
	 * Saves the map to the given directory by saving the {@link #getGraph()
	 * Graph} and the {@link #getEdgeBasedGraph() edge-based graph} to the files
	 * {@code &lt;name&gt;.graph} and {@code &lt;name&gt;.egraph}.
	 * 
	 * @param directory
	 *            The directory to which the map should be saved.
	 * @throws IOException
	 *             If the files can’t be written.
	 * @see #load(File)
	 */
	public void save(File directory) throws IOException {
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(directory.toString()
					+ " is not a directory!");
		}
		graph.save(new File(directory, directory.getName() + ".graph"));
		edgeBasedGraph
				.save(new File(directory, directory.getName() + ".egraph"));
	}

	/**
	 * Loads a map from the given directory by loading the {@link #getGraph()
	 * Graph} and the {@link #getEdgeBasedGraph() edge-based graph} from the
	 * files {@code &lt;name&gt;.graph} and {@code &lt;name&gt;.egraph}.
	 * 
	 * @param directory
	 *            The directory from which the map should be loaded.
	 * @return A {@link StreetMap} containing the {@link Graph} and
	 *         {@link EdgeBasedGraph} from the given directory.
	 * @throws IOException
	 *             If the files can’t be read.
	 */
	public static StreetMap load(File directory) throws IOException {
		Graph graph = Graph.load(new File(directory, directory.getName()
				+ ".graph"));
		EdgeBasedGraph egraph = EdgeBasedGraph.load(new File(directory,
				directory.getName() + ".egraph"));
		StreetMap ret = new StreetMap(graph, egraph);
		ret.setName(directory.getName());
		return ret;
	}

	/**
	 * Loads a map from the given directory in the same way as
	 * {@link #load(File)}, except that the {@link Graph} and
	 * {@link EdgeBasedGraph} are loaded lazily – only on the first access.
	 * 
	 * @param directory
	 *            The directory from which the map should be loaded.
	 * @return A kind of {@link StreetMap} that loads the {@link Graph} and the
	 *         {@link EdgeBasedGraph} lazily.
	 */
	public static StreetMap loadLazily(File directory) {
		return new LazyStreetMap(directory.getName(), new File(directory,
				directory.getName() + ".graph"), new File(directory,
				directory.getName() + ".egraph"));
	}

	private static class LazyStreetMap extends StreetMap {
		private final File graphFile;
		private final File egraphFile;

		public LazyStreetMap(String name, File graphFile, File egraphFile) {
			super(null, null);
			this.name = name;
			this.graphFile = graphFile;
			this.egraphFile = egraphFile;
		}

		@Override
		public Graph getGraph() {
			if (this.graph == null) {
				try {
					this.graph = Graph.load(graphFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return graph;
		}

		@Override
		public EdgeBasedGraph getEdgeBasedGraph() {
			if (this.edgeBasedGraph == null) {
				try {
					this.edgeBasedGraph = EdgeBasedGraph.load(egraphFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return edgeBasedGraph;
		}
	}
}
