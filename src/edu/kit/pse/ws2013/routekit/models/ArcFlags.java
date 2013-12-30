package edu.kit.pse.ws2013.routekit.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
	public int getFlag(int turn) {
		return flags[turn];
	}

	public void save(DataOutputStream os) throws IOException {
		os.writeInt(flags.length);
		for (int i = 0; i < flags.length; i++) {
			os.writeInt(flags[i]);
		}
	}

	public static ArcFlags load(DataInputStream is) throws IOException {
		int[] flags = new int[is.readInt()];
		for (int i = 0; i < flags.length; i++) {
			flags[i] = is.readInt();
		}
		return new ArcFlags(flags);
	}
}
