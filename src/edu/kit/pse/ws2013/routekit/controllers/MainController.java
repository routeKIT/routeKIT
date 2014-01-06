package edu.kit.pse.ws2013.routekit.controllers;

import java.io.File;

import edu.kit.pse.ws2013.routekit.history.History;
import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.mapdisplay.OSMRenderer;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileRenderer;
import edu.kit.pse.ws2013.routekit.mapdisplay.TileSource;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.RouteModel;
import edu.kit.pse.ws2013.routekit.precalculation.PreCalculator;
import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.util.Coordinates;
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
	MainView view;
	private ProfileMapCombination pmc; // TODO initialize, use – Lucas

	/**
	 * Konstruktor: Erstellt den Controller, lädt Profil, die Namen der Karte
	 * und die aktuelle Karte vollständig und erstellt dann die {@link MainView}
	 * . Für den genauen Ablauf siehe \abbildung{sequenz_start}.
	 */
	private MainController() {
		instance = this;
		view = new MainView(rm);
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
	}

	/**
	 * Startet einen neuen {@link ProfileManagerController} und öffnet so den
	 * Dialog zur Profilverwaltung.
	 */
	public void manageProfiles() {
		new ProfileManagerController(view);
	}

	/**
	 * Startet einen neuen {@link MapManagerController} und öffnet so den Dialog
	 * zur Kartenverwaltung.
	 */
	public void manageMaps() {
		new MapManagerController(view);
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
		return null;
	}

	/**
	 * Wählt das angegebene Profil aus.
	 * 
	 * @param profile
	 *            Das aktuelle Profil.
	 */
	public void selectProfile(Profile profile) {
		// TODO must set pmc – Lucas
	}

	/**
	 * Wählt die angegebene Karte aus.
	 * 
	 * @param map
	 *            Die aktuelle Karte.
	 */
	public void selectMap(StreetMap map) {
		// TODO must set pmc – Lucas
	}

	/**
	 * Gets the current profile and map.
	 * 
	 * @return The current {@link ProfileMapCombination}.
	 */
	public ProfileMapCombination getCurrentProfileMap() {
		return pmc;
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
