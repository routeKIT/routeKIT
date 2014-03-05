package edu.kit.pse.ws2013.routekit.map;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import edu.kit.pse.ws2013.routekit.models.ProgressReporter;
import edu.kit.pse.ws2013.routekit.util.DummyProgressReporter;

/**
 * A street map.
 */
public class StreetMap {
	protected String name;
	protected Graph graph;
	protected EdgeBasedGraph edgeBasedGraph;
	private final boolean isDefault;

	/**
	 * Creates a new {@code StreetMap} object from the given {@link Graph} and
	 * {@link EdgeBasedGraph}.
	 * 
	 * @param graph
	 *            the map graph
	 * @param edgeBasedGraph
	 *            the edge-based graph
	 */
	public StreetMap(Graph graph, EdgeBasedGraph edgeBasedGraph) {
		this(graph, edgeBasedGraph, false);
	}

	private StreetMap(Graph graph, EdgeBasedGraph edgeBasedGraph,
			boolean isDefault) {
		this.graph = graph;
		this.edgeBasedGraph = edgeBasedGraph;
		this.isDefault = isDefault;
	}

	/**
	 * Determines if this is a default map.
	 * 
	 * @return {@code true} if the map is a default map, else {@code false}.
	 */
	public boolean isDefault() {
		return isDefault;
	}

	/**
	 * Returns the {@link EdgeBasedGraph} of this street map.
	 * 
	 * @return the edge-based graph
	 */
	public EdgeBasedGraph getEdgeBasedGraph() {
		return edgeBasedGraph;
	}

	/**
	 * Returns the {@link Graph} of this street map.
	 * 
	 * @return the graph
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * Returns the name of this map.
	 * 
	 * @return the name, or {@code null} if it has not been set yet
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this map.
	 * 
	 * @param name
	 *            the name to be set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Ensures that this {@link StreetMap} is completely loaded. This is a noop
	 * for normal maps.
	 * 
	 * @param reporter
	 *            A {@link ProgressReporter} to report loading progress to.
	 * @see #loadLazily(File)
	 */
	public void ensureLoaded(ProgressReporter reporter) {
		reporter.setSubTasks(new float[] { .9f, .1f });
		reporter.pushTask("Lade Graphen");
		getGraph();
		reporter.nextTask("Lade kantenbasierten Graphen");
		getEdgeBasedGraph();
		reporter.popTask();
	}

	@Override
	public String toString() {
		return name;
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
		Properties p = new Properties();
		p.setProperty("default", Boolean.toString(isDefault()));
		try (FileWriter writer = new FileWriter(new File(directory,
				directory.getName() + ".properties"))) {
			p.store(writer, null);
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
		Properties p = new Properties();
		try (FileReader reader = new FileReader(new File(directory,
				directory.getName() + ".properties"))) {
			p.load(reader);
		} catch (IOException e) {
			// ignore, defaults to false
		}
		Graph graph = Graph.load(new File(directory, directory.getName()
				+ ".graph"), new DummyProgressReporter());
		EdgeBasedGraph egraph = EdgeBasedGraph.load(new File(directory,
				directory.getName() + ".egraph"));
		StreetMap ret = new StreetMap(graph, egraph, Boolean.parseBoolean(p
				.getProperty("default", "false")));
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
		Properties p = new Properties();
		try (FileReader reader = new FileReader(new File(directory,
				directory.getName() + ".properties"))) {
			p.load(reader);
		} catch (IOException e) {
			// ignore, defaults to false
		}
		return new LazyStreetMap(directory.getName(), new File(directory,
				directory.getName() + ".graph"), new File(directory,
				directory.getName() + ".egraph"), Boolean.parseBoolean(p
				.getProperty("default", "false")));
	}

	private static class LazyStreetMap extends StreetMap {
		private final File graphFile;
		private final File egraphFile;

		public LazyStreetMap(String name, File graphFile, File egraphFile,
				boolean isDefault) {
			super(null, null, isDefault);
			this.name = name;
			this.graphFile = graphFile;
			this.egraphFile = egraphFile;
		}

		@Override
		public Graph getGraph() {
			if (this.graph == null) {
				try {
					this.graph = Graph.load(graphFile,
							new DummyProgressReporter());
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

		@Override
		public void ensureLoaded(ProgressReporter reporter) {
			reporter.setSubTasks(new float[] { .9f, .1f });
			reporter.pushTask("Lade Graphen");
			if (graph == null) {
				try {
					this.graph = Graph.load(graphFile, reporter);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			reporter.nextTask("Lade kantenbasierten Graphen");
			getEdgeBasedGraph();
			reporter.popTask();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StreetMap) {
			StreetMap other = (StreetMap) obj;
			return name.equals(other.getName())
					&& isDefault == other.isDefault()
					&& getGraph().equals(other.getGraph())
					&& getEdgeBasedGraph().equals(other.getEdgeBasedGraph());
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hashcode = name.hashCode();
		Graph g = getGraph();
		EdgeBasedGraph e = getEdgeBasedGraph();
		if (g != null) {
			hashcode ^= g.hashCode();
		}
		if (e != null) {
			hashcode ^= e.hashCode();
		}
		return hashcode;
	}
}
