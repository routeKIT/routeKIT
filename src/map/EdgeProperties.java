package map;
import profiles.Profile;
/**
 * Kapselt die Eigenschaften einer Kante.
 */
public class EdgeProperties {
	/**
	 * Bestimmt die zulässige Höchstgeschwindigkeit (in Kilometern pro Stunde)
	 * auf dieser Kante für das angegebene Profil.
	 * 
	 * @param profile
	 *            Das Profil, für das die Höchstgeschwindigkeit auf dieser Kante
	 *            bestimmt werden soll.
	 * @return
	 */
	public int getMaxSpeed(Profile profile) {
		return 0;
	}
	/**
	 * Konstruktor: Erzeugt ein neues Objekt mit den angegebenen Eigenschaften.
	 * 
	 * @param type
	 *            Der Straßentyp.
	 * @param name
	 *            Der Wert für {@code name}.
	 * @param roadRef
	 *            Der Wert für {@code roadRef}.
	 * @param maxSpeed
	 *            Die zulässige Höchstgeschwindigkeit für diese Kante oder
	 *            {@code 0}, falls nicht festgelegt.
	 */
	public EdgeProperties(HighwayType type, String name, String roadRef,
			int maxSpeed) {
	}
}
