package edu.kit.pse.ws2013.routekit.controllers;
import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.views.ProfileManagerView;
/**
 * Der Controller für die {@link ProfileManagerView}.
 * 
 * Ein Beispiel für die Kommunikation zwischen den beiden Klassen ist in
 * \abbildung{sequenz_profilVerwaltung} zu sehen.
 */
public class ProfileManagerController {
	/**
	 * Wechselt zu dem temporären Profil mit dem angegebenen Namen. Falls noch
	 * kein Profil mit diesem Namen existiert, wird es als Kopie des aktuellen
	 * Profils erstellt.
	 * 
	 * Die Änderung wird der View über
	 * {@link ProfileManagerView#setCurrentProfile} mitgeteilt.
	 * 
	 * @param name
	 *            Der Name des neuen Profils.
	 */
	public void changeTemporaryProfile(String name) {
	}
	/**
	 * Markiert das aktuell ausgewählte Profil zur Löschung und entfernt es aus
	 * der Auswahlliste.
	 * 
	 * Beachte: Das Profil wird erst in
	 * {@link ProfileManagerController#saveAllChanges} tatsächlich gelöscht.
	 * 
	 * Handelt es sich bei dem aktuell ausgewählten Profil um ein
	 * Standardprofil, so wird eine {@code IllegalStateException} geworfen.
	 */
	public void deleteCurrentTemporaryProfile() {
	}
	/**
	 * Speichert die Werte des temporären Profils. Wird üblicherweise direkt vor
	 * {@link ProfileManagerController#changeTemporaryProfile} aufgerufen.
	 * 
	 * @param profile
	 *            Das temporäre Profil mit den aktuell eingegebenen Werten.
	 */
	public void saveTemporaryProfile(Profile profile) {
	}
	/**
	 * Führt alle vom Benutzer vorgenommenen Änderungen aus. Dazu gehören das
	 * Hinzufügen, Ändern und Löschen von Profilen. Für geänderte Profil werden
	 * alle Vorberechnung gelöscht.
	 */
	public void saveAllChanges() {
	}
	/**
	 * Gibt zurück, wie viel Zeit die Vorberechnung benötigt haben, die durch
	 * die aktuell erfassten Änderungen gelöscht werden. Die Dauer wird in
	 * Millisekunden zurückgegeben.
	 * 
	 * @return
	 */
	public int getDeletionTime() {
		return 0;
	}
}
