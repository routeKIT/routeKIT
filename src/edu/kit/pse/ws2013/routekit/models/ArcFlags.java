package edu.kit.pse.ws2013.routekit.models;

/**
 * Enthält die Arc-Flags für den vorberechneten Graphen.
 */
public class ArcFlags {

	/**
	 * Die Arc-Flags des Graphen (als long-Bitvektoren).
	 */
	private int[] flags;

	/**
	 * Erstellt ein neues ArcFlags-Objekt mit den übergebenen Arc-Flags.
	 * 
	 * @param flags
	 *            Die Arc-Flags.
	 */
	public ArcFlags(int[] flags) {
		this.flags = flags;
	}

	/**
	 * Gibt die Arc-Flags des angegebenen Abbiegevorgangs zurück.
	 * 
	 * @param turn
	 *            Die Nummer des Abbiegevorgangs.
	 * @return Die Flags
	 */
	int getFlag(int turn) {
		return flags[turn];
	}
}
