package profiles;
import java.io.File;
/**
 * Ein Fahrzeugprofil.
 */
public class Profile {
	/**
	 * Speichert das Profil in die angegebene Datei.
	 * 
	 * @param file
	 *            Die Datei, in die das Profil gespeichert wird.
	 */
	public void save(File file) {
	}
	/**
	 * Gibt an, ob es sich um ein Standardprofil handelt oder nicht.
	 * 
	 * @return
	 */
	public boolean isDefault() {
		return false;
	}
	/**
	 * (statisch) Lädt ein Profil aus der angegebenen Datei und gibt es zurück.
	 * 
	 * @param file
	 *            Die Datei, aus der das Profil geladen wird.
	 * @return
	 */
	public Profile load(File file) {
		return null;
	}
}
