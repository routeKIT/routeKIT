package views;
import java.util.Set;

import map.StreetMap;
import profiles.Profile;
/**
 * Zeigt das Fenster der Kartenverwaltung auf dem Bildschirm an.
 */
public class MapManagerView {
	/**
	 * Setzt die Karte, die aktuell ausgewählt werden können.
	 * 
	 * @param maps
	 *            Die verfügbaren Karte.
	 */
	public void setAvailableMaps(Set<StreetMap> maps) {
	}
	/**
	 * Setzt die aktuelle Karte auf die angegebene Karte, aktualisiert die Liste
	 * der Profil für die ausgewählte Karte und aktiviert/deaktiviert die
	 * „Import“- und „Löschen“-Buttons, je nachdem, ob es sich um eine
	 * Standardkarte handelt oder nicht.
	 * 
	 * @param map
	 *            Die neue Karte.
	 * @param profiles
	 *            Die Profil für die neue Karte.
	 */
	public void setCurrentMap(StreetMap map, Set<Profile> profiles) {
	}
}
