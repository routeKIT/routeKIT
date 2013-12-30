package edu.kit.pse.ws2013.routekit.routecalculation;

import java.util.List;

/**
 * Kapselt die zu einer {@link Route} gehörende Wegbeschreibung.
 */
public class RouteDescription {
	/**
	 * Liefert eine Liste der Abbiegeanweisungen.
	 * 
	 * @return
	 */
	public List<TurnInstruction> getInstructions() {
		return null;
	}

	/**
	 * Konstruktor: Erzeugt ein neues Objekt mit den angegebenen Parametern.
	 * 
	 * @param route
	 *            Die Route, die die Wegbeschreibung beschreibt.
	 * @param instructions
	 *            Eine Liste von Abbiegeanweisungen.
	 */
	public RouteDescription(Route route, List<TurnInstruction> instructions) {
	}
}
