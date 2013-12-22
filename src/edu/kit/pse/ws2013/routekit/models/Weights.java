package edu.kit.pse.ws2013.routekit.models;

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
}
