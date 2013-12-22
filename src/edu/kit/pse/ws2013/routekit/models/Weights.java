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
	 * Erstellt ein neues Weights-Objekt für die angegebene Anzahl an
	 * Abbiegevorgängen. Alle Kantengewichte sind 0.
	 * 
	 * @param numberOfEdges
	 *            Die Anzahl der Abbiegevorgänge.
	 */
	public Weights(int numberOfTurns) {
		weights = new int[numberOfTurns];
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

	/**
	 * Setzt das Kantengewicht des angegebenen Abbiegevorgangs auf den
	 * gewünschten Wert.
	 * 
	 * @param turn
	 *            Die Nummer des Abbiegevorgangs.
	 * @param weight
	 *            Das gewünschte Kantengewicht.
	 */
	public void setWeight(int turn, int weight) {
		weights[turn] = weight;
	}
}
