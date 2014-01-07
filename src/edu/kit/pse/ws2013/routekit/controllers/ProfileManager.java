package edu.kit.pse.ws2013.routekit.controllers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * Verwaltet die Profil. Hat intern eine Menge von vorhandenen Profilen.
 */
public class ProfileManager {

	private static ProfileManager instance;

	private ProfileManager() {
		// TODO implement
	}

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
		// TODO this is only a dummy implementation
		return new HashSet<>(Arrays.asList(new Profile[] { Profile.defaultCar,
				Profile.defaultTruck }));
	}

	/**
	 * Initializes the {@link ProfileManager}.
	 * 
	 * @throws IllegalStateException
	 *             If the ProfileManager is already initialized.
	 */
	public static void init() {
		if (instance != null) {
			throw new IllegalStateException("Already initialized!");
		}
		instance = new ProfileManager();
	}

	/**
	 * Returns the {@link ProfileManager} instance. This is only allowed if the
	 * ProfileManager was previously {@link #init() initialized}.
	 * 
	 * @return The ProfileManager instance.
	 * @throws IllegalStateException
	 *             If the ProfileManager is uninitialized.
	 */
	public static ProfileManager getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Not yet initialized!");
		}
		return instance;
	}
}
