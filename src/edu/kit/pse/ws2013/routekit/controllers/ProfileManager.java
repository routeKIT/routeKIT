package edu.kit.pse.ws2013.routekit.controllers;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.profiles.Profile;
/**
 * Verwaltet die Profil. Hat intern eine Menge von vorhandenen Profilen.
 */
public class ProfileManager {
	/**
	 * Löscht das ausgewählte Profil aus der internen Liste und von der
	 * Festplatte.
	 * 
	 * @param profile
	 *            Das Profil, das gelöscht werden soll.
	 */
	public void deleteProfile(Profile profile) {
	}
	/**
	 * Speichert das ausgewählte Profil in der internen Liste und auf der
	 * Festplatte.
	 * 
	 * (Der Speicherort wird vom Manager deckend verwaltet.)
	 * 
	 * @param profile
	 *            Das Profil, das gespeichert werden soll.
	 */
	public void saveProfile(Profile profile) {
	}
	/**
	 * Gibt alle Profil in der internen Liste zurück.
	 * 
	 * @return
	 */
	public Set<Profile> getProfiles() {
		return null;
	}
}
