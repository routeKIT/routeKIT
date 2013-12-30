package edu.kit.pse.ws2013.routekit.models;

import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * Eine Kombination aus einem {@link Profile Profil} und einer {@link StreetMap
 * Karte}.
 */
public class ProfileMapCombination {
	private StreetMap map;
	private Profile p;
	private Weights weights;
	private ArcFlags arc;

	public ProfileMapCombination(StreetMap map, Profile p) {
		this.map = map;
		this.p = p;
	}

	/**
	 * Gibt {@code true} zurück, wenn für eine Kombination aus Profil und Karte
	 * eine Vorberechnung der Gewichte und der Arc-Flags existiert.
	 * 
	 * @return
	 */
	public boolean isCalculated() {
		return weights != null && arc != null;
	}

	public StreetMap getStreetMap() {
		return map;
	}

	public Profile getProfile() {
		return p;
	}

	public Weights getWeights() {
		return weights;
	}

	public ArcFlags getArc() {
		return arc;
	}

	public void setWeights(Weights weights) {
		this.weights = weights;
	}

	public void setArcFlags(ArcFlags arcFlags) {
		arc = arcFlags;

	}
}
