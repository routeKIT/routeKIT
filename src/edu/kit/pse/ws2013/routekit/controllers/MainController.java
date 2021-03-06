package edu.kit.pse.ws2013.routekit.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;

import edu.kit.pse.ws2013.routekit.export.GPXExporter;
import edu.kit.pse.ws2013.routekit.export.HTMLExporter;
import edu.kit.pse.ws2013.routekit.history.History;
import edu.kit.pse.ws2013.routekit.history.HistoryEntry;
import edu.kit.pse.ws2013.routekit.map.GraphIndex;
import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.mapdisplay.OSMRenderer;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileCache;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileRenderer;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileSource;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;
import edu.kit.pse.ws2013.routekit.models.RouteModel;
import edu.kit.pse.ws2013.routekit.precalculation.PreCalculator;
import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.routecalculation.ArcFlagsDijkstra;
import edu.kit.pse.ws2013.routekit.routecalculation.Dijkstra;
import edu.kit.pse.ws2013.routekit.routecalculation.Route;
import edu.kit.pse.ws2013.routekit.routecalculation.RouteCalculator;
import edu.kit.pse.ws2013.routekit.routecalculation.RouteDescription;
import edu.kit.pse.ws2013.routekit.routecalculation.RouteDescriptionGenerator;
import edu.kit.pse.ws2013.routekit.util.Coordinates;
import edu.kit.pse.ws2013.routekit.util.FileUtil;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;
import edu.kit.pse.ws2013.routekit.views.MainView;
import edu.kit.pse.ws2013.routekit.views.ProgressDialog;

/**
 * routeKIT’s main Controller. Manages the program execution and remains alive
 * until routeKIT exits.
 */
public class MainController {
	private final GPXExporter gpxExporter = new GPXExporter();
	private final HTMLExporter htmlExporter = new HTMLExporter();
	private static MainController instance;
	private final ProfileMapManager profileMapManager;
	private RouteModel rm = new RouteModel();
	private final History history;
	MainView view;
	private RouteCalculator rc;
	private RouteDescriptionGenerator rdg;
	private boolean useOnlineMaps = false;
	private TileCache cache = null;

	/**
	 * Creates the controller, initializes the {@link MapManager},
	 * {@link ProfileManager} and {@link ProfileMapManager} and then creates the
	 * {@link MainView}.
	 * 
	 * @param pr
	 *            the starter
	 */
	private MainController(ProgressReporter pr) {
		pr.pushTask("Starte routeKIT");
		pr.setSubTasks(new float[] { 0.99f, 0.01f });
		instance = this;
		pr.pushTask("Lese Index");
		try {
			ProfileMapManager.init(FileUtil.getRootDir(), pr);
		} catch (IOException e) {
			// die
			history = null;
			profileMapManager = null;
			return;
		}
		profileMapManager = ProfileMapManager.getInstance();
		profileMapManager.getCurrentCombination().ensureLoaded(pr);
		pr.popTask("Lese Index");

		profileMapManager.addCurrentCombinationListener(rm);
		History _history; // because history is final
		try {
			_history = History.load(FileUtil.getHistoryFile());
		} catch (IOException e) {
			_history = new History();
		}
		history = _history;
		rc = new ArcFlagsDijkstra();
		rdg = new RouteDescriptionGenerator();
		pr.pushTask("Lade Ansicht");
		view = new MainView(rm);
		pr.popTask();
		pr.popTask();
	}

	/**
	 * Called when the start point changes (e.&nbsp;g. via user input). Sets the
	 * new start point in the {@link RouteModel}. If a destination point is
	 * present, adds an entry to the {@link History} and starts route
	 * calculation.
	 * 
	 * @param start
	 *            The coordinates of the new start point.
	 */
	public void setStartPoint(Coordinates start) {
		rm.setStart(start);

		checkAndCalculate();
	}

	/**
	 * Called when the destination point changes (e.&nbsp;g. via user input).
	 * Sets the new destination point in the {@link RouteModel}. If a start
	 * point is present, adds an entry to the {@link History} and starts route
	 * calculation.
	 * 
	 * @param destination
	 *            The coordinates of the new destination point.
	 */
	public void setDestinationPoint(Coordinates destination) {
		rm.setDestination(destination);

		checkAndCalculate();
	}

	Thread calculator;

	/**
	 * If a start and destination point are present, adds a history entry and
	 * starts route calculation.
	 */
	private void checkAndCalculate() {
		final Coordinates start = rm.getStart();
		final Coordinates destination = rm.getDestination();
		if (start != null && destination != null) {
			List<HistoryEntry> entries = history.getEntries();
			boolean saveNewEntry = true;
			if (!entries.isEmpty()) {
				HistoryEntry last = entries.get(entries.size() - 1);
				if (last != null && last.getStart().equals(start)
						&& last.getDest().equals(destination)) {
					saveNewEntry = false;
				}
			}
			if (saveNewEntry) {
				history.addEntry(start, destination);
				try {
					history.save(FileUtil.getHistoryFile());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			rm.setCurrentRoute(null);
			calculator = new Thread("Route calc") {
				{
					setDaemon(true);
				}

				@Override
				public void run() {
					rm.startCalculating();
					ProfileMapCombination currentCombination = profileMapManager
							.getCurrentCombination();
					GraphIndex index = currentCombination.getStreetMap()
							.getGraph().getIndex(18);
					PointOnEdge startPointOnEdge = index
							.findNearestPointOnEdge(start);
					PointOnEdge destinationPointOnEdge = index
							.findNearestPointOnEdge(destination);
					if (startPointOnEdge == null
							|| destinationPointOnEdge == null) {
						rm.setCurrentRoute(null);
						rm.setCurrentDescription(null);
						return;
					}
					Route r = rc.calculateRoute(startPointOnEdge,
							destinationPointOnEdge, currentCombination);
					if (calculator != this) {
						return;
					}
					rm.setCurrentRoute(r);

					RouteDescription rd = r == null ? null : rdg
							.generateRouteDescription(r);
					rm.setCurrentDescription(rd);

				}
			};
			calculator.start();
		}
	}

	/**
	 * Saves the description of the current route in HTML format into the given
	 * file. If no current route is available (e.&nbsp;g. because no
	 * precalculation has been executed), an {@link IllegalStateException} is
	 * thrown.
	 * 
	 * @param target
	 *            The file into which the description shall be saved.
	 */
	public void exportHTML(File target) {
		Route route = rm.getCurrentRoute();
		if (route == null) {
			throw new IllegalStateException("No current route to export!");
		}
		RouteDescription description = rdg.generateRouteDescription(route);
		try {
			htmlExporter.exportRouteDescription(description, target);
		} catch (IOException e) {
			view.textMessage("Beim Export der Routenbeschreibung ist ein Fehler aufgetreten!\n"
					+ e.getMessage());
		}
	}

	/**
	 * Called when the start and destination point change, e.&nbsp;g. by
	 * selecting a {@link HistoryEntry}. The same actions as for
	 * {@link #setStartPoint(Coordinates)} and
	 * {@link #setDestinationPoint(Coordinates)} are executed, but only once.
	 * 
	 * @param start
	 *            The coordinates of the new start point.
	 * @param destination
	 *            The coordinates of the new destination point.
	 */
	public void setStartAndDestinationPoint(Coordinates start,
			Coordinates destination) {
		rm.setStart(start);
		rm.setDestination(destination);

		checkAndCalculate();
	}

	/**
	 * Calls {@link PreCalculator#doPrecalculation(ProfileMapCombination)} in a
	 * new worker thread if no precalculation for the current
	 * {@link ProfileMapCombination} exists.
	 * <p>
	 * The given {@link ProgressReporter} should already have the task
	 * "Precalculating and saving" or something similar pushed onto it. This
	 * method will then push and pop sub-tasks.
	 * <p>
	 * The changes are executed asynchronously in a new worker thread, and after
	 * all changes have been executed, an additional task is popped off the
	 * reporter that this method did not push (the task
	 * "Precalculating and saving", as mentioned earlier). This way, the caller
	 * may be notified when the changes are done.
	 * 
	 * @param reporter
	 *            The {@link ProgressReporter} to which progress shall be
	 *            reported.
	 */
	public void startPrecalculation(final ProgressReporter reporter) {
		new Thread("MainController Precalculation Thread") {
			{
				setDaemon(true);
			}

			@Override
			public void run() {
				ProfileMapCombination combination = profileMapManager
						.getCurrentCombination();
				if (!combination.isCalculated()) {
					reporter.setSubTasks(new float[] { .95f, .05f });
					reporter.pushTask("Führe Vorberechnung durch für '"
							+ combination + "'");
					new PreCalculator().doPrecalculation(combination, reporter);
					reporter.nextTask("Speichere '" + combination + "'");
					profileMapManager.savePrecalculation(combination);
					reporter.popTask();
				}
				reporter.popTask();
			};
		}.start();
	}

	/**
	 * Determines whether OSM tiles or our own tiles shall be used for
	 * rendering. For OSM tiles, the {@link OSMRenderer} is used; for our own
	 * tiles, {@link TileRenderer}.
	 * 
	 * @param useOnlineMaps
	 *            {@code true} to use OSM tiles, {@code false} to use our own
	 *            tiles.
	 * 
	 */
	public void setUseOnlineMaps(boolean useOnlineMaps) {
		this.useOnlineMaps = useOnlineMaps;
	}

	public void setUseArcFlags(boolean useArcFlags) {
		if (useArcFlags) {
			rc = new ArcFlagsDijkstra();
		} else {
			rc = new Dijkstra();
		}
		// TODO: Direkt neu berechnen oder nicht?
		// checkAndCalculate();
	}

	/**
	 * Creates a new {@link ProfileManagerController}, which opens the profile
	 * management dialog.
	 */
	public void manageProfiles() {
		profileMapManager.pauseEvents();
		ProfileManagerController c = new ProfileManagerController(view);
		Profile selected = c.getSelectedProfile();
		ProfileMapCombination current = profileMapManager
				.getCurrentCombination();
		if (selected != null && !selected.equals(current.getProfile())) {
			load(selected, current.getStreetMap());
		}
		profileMapManager.resumeEvents();
	}

	MapManagerController mapManagement;

	/**
	 * Creates a new {@link MapManagerController}, which opens the map
	 * management dialog.
	 */
	public void manageMaps() {
		profileMapManager.pauseEvents();
		mapManagement = new MapManagerController(view);
		StreetMap selected = mapManagement.getSelectedMap();
		ProfileMapCombination current = profileMapManager
				.getCurrentCombination();
		if (selected != null) {
			load(current.getProfile(), selected);
		}
		profileMapManager.resumeEvents();
	}

	private void load(Profile profile, StreetMap map) {
		ProfileMapCombination newCombination = profileMapManager
				.getPrecalculation(profile, map);
		if (newCombination == null) {
			newCombination = new ProfileMapCombination(map, profile);
		}
		final ProfileMapCombination theNewCombination = newCombination;
		ProgressDialog p = new ProgressDialog(view);
		final ProgressReporter reporter = new ProgressReporter();
		reporter.addProgressListener(p);
		reporter.pushTask("Lade ausgewählte Karte und Vorberechnung");
		new Thread("Load map + precalculation") {
			{
				setDaemon(true);
			}

			@Override
			public void run() {
				theNewCombination.ensureLoaded(reporter);
				reporter.popTask();
			};
		}.start();
		p.setVisible(true);
		profileMapManager.setCurrentCombination(newCombination);
	}

	/**
	 * Saves the current {@link Route} in the GPS Exchange Format into the given
	 * file. If there is no current route, an {@link IllegalStateException} is
	 * thrown.
	 * 
	 * @param target
	 *            The file into which the route shall be exported.
	 */
	public void exportGPX(File target) {
		Route route = rm.getCurrentRoute();
		if (route == null) {
			throw new IllegalStateException("No current route to export!");
		}
		try {
			gpxExporter.exportRoute(route, target);
		} catch (FileNotFoundException | XMLStreamException e) {
			// TODO the view should display this error
			e.printStackTrace();
		}
	}

	/**
	 * Returns a {@link TileSource} that should be used to render tiles.
	 * 
	 * @return A {@link TileSource} for rendering tiles.
	 */
	public TileSource getTileSource() {
		if (cache != null) {
			cache.stop();
		}
		if (useOnlineMaps) {
			String tileServer = FileUtil.getTileServer();
			if (tileServer != null) {
				try {
					return cache = new TileCache(new OSMRenderer(tileServer));
				} catch (IOException e) {
					// OSMRenderer couldn’t construct, server not reachable
					JOptionPane
							.showMessageDialog(
									view,
									"Der Server für die Onlinekacheln kann nicht verwendet werden.\n"
											+ "Bitte überprüfen Sie den Inhalt der Datei tileServer.txt im Ordner "
											+ FileUtil.getRootDir()
											+ "\noder verwenden sie einen anderen Server,\n"
											+ "zum Beispiel http://[abc].tile.openstreetmap.org/.",
									"Kachelserver nicht verwendbar",
									JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane
						.showMessageDialog(
								view,
								"Es wurde noch kein Server für die Onlinekacheln eingestellt.\n"
										+ "Bitte legen Sie eine Datei tileServer.txt im Ordner "
										+ FileUtil.getRootDir()
										+ " an,\n"
										+ "zum Beispiel mit dem Inhalt http://[abc].tile.openstreetmap.org/.",
								"Kein Kachelserver", JOptionPane.ERROR_MESSAGE);
			}
			// fallthrough / fallback to own renderer
		}
		return cache = new TileCache(new TileRenderer(profileMapManager
				.getCurrentCombination().getStreetMap().getGraph()));
	}

	/**
	 * Gets the history.
	 * <p>
	 * (Please don’t modify it.)
	 * 
	 * @return The history.
	 */
	public History getHistory() {
		return history;
	}

	public static MainController getInstance() {
		if (instance == null) {
			throw new Error("hey");
		}
		return instance;
	}

	/**
	 * Main method of the program. Creates the {@link MainController}.
	 * 
	 * @param args
	 *            Command line arguments (currently unused).
	 */
	public static void main(String[] args) {
		if (args.length != 0) {
			new TerminalCLI(args).run();
			return;
		}

		final ProgressDialog pd = new ProgressDialog(null);
		new Thread("Upstart") {
			{
				setDaemon(true);
			}

			@Override
			public void run() {
				ProgressReporter pr = new ProgressReporter();
				pr.addProgressListener(pd);
				instance = new MainController(pr);
			}
		}.start();
		pd.setVisible(true);
		instance.view.setVisible(true);
	}
}
