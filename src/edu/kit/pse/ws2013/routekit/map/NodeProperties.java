package edu.kit.pse.ws2013.routekit.map;

/**
 * Kapselt die Eigenschaften eines Knotens.
 */
public class NodeProperties {
	/**
	 * Gibt die Nummer der Anschlussstelle zurück oder {@code null}, falls es
	 * sich nicht um eine Anschlussstelle handelt.
	 * 
	 * @return
	 */
	public String getJunctionRef() {
		return null;
	}

	/**
	 * Gibt den Namen der Anschlussstelle zurück oder {@code null}, falls es
	 * sich nicht um eine Anschlussstelle handelt.
	 * 
	 * @return
	 */
	public String getJunctionName() {
		return null;
	}

	/**
	 * Bestimmt, ob der Knoten eine Schnellstraßen- oder Autobahnanschlussstelle
	 * ist.
	 * 
	 * @return
	 */
	public boolean isMotorwayJunction() {
		return false;
	}

	/**
	 * Bestimmt, ob sich an dem Knoten eine Ampel befindet.
	 * 
	 * @return
	 */
	public boolean isTrafficLights() {
		return false;
	}
}
