package edu.kit.pse.ws2013.routekit.controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.kit.pse.ws2013.routekit.history.History;
import edu.kit.pse.ws2013.routekit.history.HistoryEntry;
import edu.kit.pse.ws2013.routekit.map.GraphIndex;
import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.mapdisplay.OSMRenderer;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileCache;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileRenderer;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileSource;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.RouteModel;
import edu.kit.pse.ws2013.routekit.precalculation.PreCalculator;
import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.routecalculation.ArcFlagsDijkstra;
import edu.kit.pse.ws2013.routekit.routecalculation.Route;
import edu.kit.pse.ws2013.routekit.routecalculation.RouteCalculator;
import edu.kit.pse.ws2013.routekit.util.Coordinates;
import edu.kit.pse.ws2013.routekit.util.FileUtil;
import edu.kit.pse.ws2013.routekit.views.MainView;
import edu.kit.pse.ws2013.routekit.views.MapView;

/**
 * Der Haupt-Controller von routeKIT. Er wird beim Programmstart erstellt und
 * erstellt dabei die {@link MainView}. Er verwaltet den gesamtem Programmablauf
 * und bleibt so lange bestehen, bis routeKIT beendet wird.
 */
public class MainController {
	private static MainController instance;
	private RouteModel rm = new RouteModel();
	private final History history;
	MainView view;
	private RouteCalculator rc;
	TileSource source;

	/**
	 * Konstruktor: Erstellt den Controller, lädt Profil, die Namen der Karte
	 * und die aktuelle Karte vollständig und erstellt dann die {@link MainView}
	 * . Für den genauen Ablauf siehe \abbildung{sequenz_start}.
	 */
	private MainController() {
		instance = this;
		try {
			ProfileMapManager.init(FileUtil.getRootDir());
		} catch (IOException e) {
			// die
			history = null;
			return;
		}
		History _history; // because history is final
		try {
			_history = History.load(FileUtil.getHistoryFile());
		} catch (IOException e) {
			_history = new History();
		}
		history = _history;
		source = new TileCache(new TileRenderer(ProfileMapManager.getInstance()
				.getCurrentCombination().getStreetMap().getGraph()));
		view = new MainView(rm);
		rc = new ArcFlagsDijkstra();
	}

	/**
	 * Wird aufgerufen, wenn sich der Startpunkt ändert (z. B. durch eine
	 * Eingabe des Benutzers). Setzt den neuen Startpunkt in der {@link MapView}
	 * . Falls bereits ein Zielpunkt ausgewählt ist, wird außerdem ein neuer
	 * Eintrag zum Verlauf hinzugefügt (siehe {@link History#addEntry}) und die
	 * Routenberechnung gestartet.
	 * 
	 * @param start
	 *            Die Koordinaten des neuen Startpunkts.
	 */
	public void setStartPoint(Coordinates start) {
		rm.setStart(start);

		checkAndCalculate();
	}

	/**
	 * Wird aufgerufen, wenn sich der Zielpunkt ändert (z. B. durch eine Eingabe
	 * des Benutzers). Setzt den neuen Zielpunkt in der {@link MapView}. Falls
	 * bereits ein Startpunkt ausgewählt ist, wird außerdem ein neuer Eintrag
	 * zum Verlauf hinzugefügt (siehe {@link History#addEntry}) und die
	 * Routenberechnung gestartet.
	 * 
	 * @param destination
	 *            Die Koordinaten des neuen Zielpunkts.
	 */
	public void setDestinationPoint(Coordinates destination) {
		rm.setDestination(destination);

		checkAndCalculate();
	}

	/**
	 * Ruft bei bedarf die Routenberechnung auf.
	 */
	private void checkAndCalculate() {
		Coordinates start = rm.getStart();
		Coordinates destination = rm.getDestination();
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
			ProfileMapCombination currentCombination = ProfileMapManager
					.getInstance().getCurrentCombination();
			GraphIndex index = currentCombination.getStreetMap().getGraph()
					.getIndex(18);
			// TODO Unblock
			Route r = rc.calculateRoute(index.findNearestPointOnEdge(start),
					index.findNearestPointOnEdge(destination),
					currentCombination);
			rm.setCurrentRoute(r);
		}
	}

	/**
	 * Speichert die Wegbeschreibung der aktuellen Route im HTML-Format in die
	 * angegeben Datei. Ist keine aktuelle Route verfügbar (z. B. da noch keine
	 * Vorberechnung vorliegt), so wird eine {@code IllegalStateException}
	 * geworfen.
	 * 
	 * @param target
	 *            Die Datei, in die die Wegbeschreibung gespeichert werden soll.
	 */
	public void exportHTML(File target) {
	}

	/**
	 * Wird aufgerufen, wenn sich der Start- und Zielpunkt ändern (z. B. durch
	 * die Auswahl eines Eintrags aus dem Verlauf). Die gleichen Aktionen wie
	 * für {@link MainController#setStartPoint} und
	 * {@link MainController#setDestinationPoint} werden ausgeführt, nur nicht
	 * doppelt.
	 * 
	 * @param start
	 *            Die Koordinaten des neuen Startpunkts.
	 * @param destination
	 *            Die Koordinaten des neuen Zielpunkts.
	 */
	public void setStartAndDestinationPoint(Coordinates start,
			Coordinates destination) {
		rm.setStart(start);
		rm.setDestination(destination);

		checkAndCalculate();
	}

	/**
	 * Ruft in einem neuen WorkerThread {@link PreCalculator#doPrecalculation}
	 * auf, falls keine Vorberechnung für diese Kombination aus Profil und Karte
	 * existiert. Sperrt währenddessen die {@link MainView}.
	 * 
	 * @param combination
	 *            Eine nicht vorberechnete Kombination aus Profil und Karte.
	 */
	public void startPrecalculation(ProfileMapCombination combination) {
	}

	/**
	 * Legt fest, ob OSM-Kachel oder selbst gerenderte Kacheln verwendet werden
	 * sollen. Für OSM-Kachel wird der {@link OSMRenderer} verwendet, für die
	 * eigenen Kacheln der {@link TileRenderer}.
	 * 
	 * @param useOnlineMaps
	 *            {@code true}, um OSM-Kachel zu verwenden, {@code false}, um
	 *            selbst gerenderte Kacheln zu verwenden.
	 */
	public void setUseOnlineMaps(boolean useOnlineMaps) {
		if (useOnlineMaps) {
			source = new TileCache(new OSMRenderer());
		} else {
			source = new TileCache(new TileRenderer(ProfileMapManager
					.getInstance().getCurrentCombination().getStreetMap()
					.getGraph()));
		}
	}

	/**
	 * Startet einen neuen {@link ProfileManagerController} und öffnet so den
	 * Dialog zur Profilverwaltung.
	 */
	public void manageProfiles() {
		ProfileManagerController c = new ProfileManagerController(view);
		Profile selected = c.getSelectedProfile();
		if (selected != null) {
			ProfileMapManager.getInstance().selectProfileAndMap(
					selected,
					ProfileMapManager.getInstance().getCurrentCombination()
							.getStreetMap());
			// TODO update view elements etc.
		}
	}

	MapManagerController mapManagement;

	/**
	 * Startet einen neuen {@link MapManagerController} und öffnet so den Dialog
	 * zur Kartenverwaltung.
	 */
	public void manageMaps() {
		mapManagement = new MapManagerController(view);
		StreetMap selected = mapManagement.getSelectedMap();
		if (selected != null) {
			ProfileMapManager.getInstance().selectProfileAndMap(
					ProfileMapManager.getInstance().getCurrentCombination()
							.getProfile(), selected);
			// TODO update view elements
		}
	}

	/**
	 * Speichert die aktuelle Route im GPS Exchange Format-Format in die
	 * angegebene Datei. Ist keine aktuelle Route verfügbar (z. B. da noch keine
	 * Vorberechnung vorliegt), so wird eine {@code IllegalStateException}
	 * geworfen.
	 * 
	 * @param target
	 *            Die Datei, in die die Route gespeichert werden soll.
	 */
	public void exportGPX(File target) {
	}

	/**
	 * Gibt eine {@link TileSource} zurück, die zum Rendern der Karte verwendet
	 * werden soll.
	 * 
	 * @return
	 */
	public TileSource getTileSource() {
		return source;
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
			new MainController();
		}
		return instance;
	}

	/**
	 * Hauptmethode des Programms. Erzeugt einen {@link MainController}.
	 * 
	 * @param args
	 *            Kommandozeilen-Argumente.
	 */
	public static void main(String[] args) {
		instance = new MainController();
	}
}
