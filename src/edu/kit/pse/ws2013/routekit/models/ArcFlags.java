package edu.kit.pse.ws2013.routekit.models;

/**
 * Enthält die Arc-Flags für den vorberechneten Graphen.
 */
public class ArcFlags {

	/**
	 * Die Arc-Flags des Graphen (als long-Bitvektoren).
	 */
	private long[] flags;

	/**
	 * Erstellt ein neues ArcFlags-Objekt mit den übergebenen Arc-Flags.
	 * 
	 * @param flags
	 *            Die Arc-Flags.
	 */
	public ArcFlags(long[] flags) {
		this.flags = flags;
	}

	/**
	 * Erstellt ein neues ArcFlags-Objekt für die angegebene Anzahl an
	 * Abbiegevorgängen. Alle flags sind 0;
	 * 
	 * @param numberOfTurns
	 *            Die Anzahl der Abbiegevorgänge.
	 */
	public ArcFlags(int numberOfTurns) {
		flags = new long[numberOfTurns];
	}

	/**
	 * Setzt die gewünschte Arc-Flag des angegebenen Abbiegevorgangs auf den
	 * gewünschten Wert.
	 * 
	 * @param turn
	 *            Die Nummer des Abbiegevorgangs.
	 * @param flagId
	 *            Die Nummer der Flag.
	 * @param value
	 *            Der gewünschte Wert.
	 */
	public void setFlag(int turn, int flagId, boolean value) {
		if (value) {
			flags[turn] |= 1 << flagId;
		} else {
			flags[turn] &= ~(1 << flagId);
		}
	}

	/**
	 * Gibt die gewünschte Arc-Flag des angegebenen Abbiegevorgangs zurück.
	 * 
	 * @param turn
	 *            Die Nummer des Abbiegevorgangs.
	 * @param flagId
	 *            Die Nummer der Flag.
	 * @return Die Flag
	 */
	boolean getFlag(int turn, int flagId) {
		return (flags[turn] & 1 << flagId) != 0;
	}
}
