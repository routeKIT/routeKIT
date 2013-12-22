package edu.kit.pse.ws2013.routekit.models;
import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.profiles.Profile;
/**
 * Eine Kombination aus einem {@link Profile Profil} und einer {@link StreetMap
 * Karte}.
 */
public class ProfileMapCombination {
	/**
	 * Gibt {@code true} zurück, wenn für eine Kombination aus Profil und Karte
	 * eine Vorberechnung der Gewichte und der Arc-Flags existiert.
	 * 
	 * @return
	 */
	public boolean isCalculated() {
		return false;
	}

	public StreetMap getStreetMap() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setWeights(Weights weights) {
		// TODO Auto-generated method stub
		
	}

	public void setArcFlags(ArcFlags arcFlags) {
		// TODO Auto-generated method stub
		
	}
}
