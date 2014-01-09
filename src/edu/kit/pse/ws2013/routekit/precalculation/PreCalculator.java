package edu.kit.pse.ws2013.routekit.precalculation;

import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * Führt die Vorberechnung für eine Kombination aus {@link Profile Profil} und
 * {@link StreetMap Karte} durch.
 */
public class PreCalculator {
	EdgeWeighter weighter;
	ArcFlagsCalculator calulator;

	public PreCalculator() {
		weighter = new EdgeWeighterImpl();
		calulator = new ArcFlagsCalculatorDummy();
	}

	/**
	 * Führt die Vorberechnung für die gegebene Kombination aus {@link Profile
	 * Profil} und {@link StreetMap Karte} durch. Dabei werden ein
	 * {@link EdgeWeighter} und ein {@link ArcFlagsCalculator} aufgerufen. Die
	 * benötigte Zeit wird in {@code calculationTime} gespeichert.
	 * 
	 * @param comb
	 *            Die Kombination aus {@link Profile Profil} und
	 *            {@link StreetMap Karte}, für die die Vorberechnung
	 *            durchgeführt werden soll.
	 */
	public void doPrecalculation(ProfileMapCombination comb) {
		weighter.weightEdges(comb);
		calulator.calculateArcFlags(comb);
	}
}
