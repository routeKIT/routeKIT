package edu.kit.pse.ws2013.routekit.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Enthält die Kantengewichte für den vorberechneten Graphen.
 */
public class Weights {
	/**
	 * Die Gewichte der Abbiegevorgänge.
	 */
	private int[] weights;

	/**
	 * Erstellt ein neues Weights-Objekt mit den übergebenen Kantengewichten.
	 * 
	 * @param weights
	 *            Die Kantengewichte.
	 */
	public Weights(int[] weights) {
		this.weights = weights;
	}

	/**
	 * Gibt das zum Abbiegevorgang gehörende Gewicht zurück.
	 * 
	 * @param turn
	 *            Die Nummer eines Abbiegevorgang.
	 * @return Das Kantengwicht des Abbiegevorgangs.
	 */
	public int getWeight(int turn) {
		return weights[turn];
	}

	public void save(DataOutputStream os) throws IOException {
		os.writeInt(weights.length);
		for (int i = 0; i < weights.length; i++) {
			os.writeInt(weights[i]);
		}
	}

	public static Weights load(DataInputStream is) throws IOException {
		int[] weights = new int[is.readInt()];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = is.readInt();
		}
		return new Weights(weights);
	}
}
