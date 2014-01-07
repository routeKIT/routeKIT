package edu.kit.pse.ws2013.routekit.controllers;

import java.io.File;

import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.views.MainView;
import edu.kit.pse.ws2013.routekit.views.MapManagerView;

/**
 * Der Controller für die {@link MapManagerView}.
 */
public class MapManagerController {
	MapManagerView mmv;

	public MapManagerController(MainView view) {
		mmv = new MapManagerView(view, this);
		mmv.setVisible(true);
	}

	/**
	 * Markiert die aktuell ausgewählte Karte zur Löschung und entfernt sie aus
	 * der Auswahlliste.
	 * 
	 * Beachte: Die Karte wird erst in
	 * {@link MapManagerController#saveAllChanges} tatsächlich gelöscht.
	 * 
	 * Handelt es sich bei der aktuell ausgewählten Karte um eine Standardkarte,
	 * so wird eine {@code IllegalStateException} geworfen.
	 */
	public void deleteCurrentMap() {
	}

	/**
	 * Entfernt das angegebene Profil von der ausgewählten Karte.
	 * 
	 * @param profile
	 *            Das Profil, das entfernt werden soll.
	 */
	public void removeProfile(String string) {
	}

	/**
	 * Führt alle vom Benutzer vorgenommenen Änderungen aus. Dazu gehören das
	 * Importieren und Löschen von Karte sowie das Hinzufügen oder Löschen von
	 * Profilen je Karte (Löscht also Vorberechnung oder erzeugt neue).
	 */
	public void saveAllChanges() {
	}

	/**
	 * Fügt das angegebene Profil zur ausgewählten Karte hinzu.
	 * 
	 * @param profile
	 *            Das neue Profil.
	 */
	public void addProfile(Profile profile) {
	}

	/**
	 * Fügt eine neue Karte mit dem angegebenen Namen hinzu (oder ersetzt eine
	 * bestehende mit diesem Namen) und wählt sie aus.
	 * 
	 * Beachte: Die Graphical User Interface-Aktionen „Importieren“ und
	 * „Aktualisieren“ werden beide durch diese Methode implementiert; bei
	 * „Importieren“ stellt die Graphical User Interface sicher, dass kein
	 * bereits existierender Name gewählt wird, bei „Aktualisieren“ verwendet
	 * sie den Namen der existierenden Karte.
	 * 
	 * @param name
	 *            Der Name der neuen Karte.
	 * @param file
	 *            Die Datei aus der sie geladen werden soll.
	 */
	public void importMap(String name, File file) {
	}

	/**
	 * Wird aufgerufen, wenn in der {@link MapManagerView} eine andere Karte
	 * ausgewählt wird. Speichert die Liste der Profil für diese Karte und setzt
	 * sie auf die Liste der neuen Karte (ggf. die bereits gespeicherte Liste,
	 * falls die Karte schon zuvor einmal ausgewählt war). Aktiviert/Deaktiviert
	 * den Löschen-Button, je nachdem, ob die neue Karte eine Standardkarte ist
	 * oder nicht.
	 * 
	 * @param map
	 *            Die neue Karte.
	 */
	public void changeMap(StreetMap map) {
	}
}
