package edu.kit.pse.ws2013.routekit.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * Manages the profiles and saves/loads them to/from disk.
 */
public class ProfileManager {

	private static ProfileManager instance;

	private final File root;
	private final Map<Profile, File> profiles;
	private final Map<String, Profile> profilesByName;

	private ProfileManager(File root) throws FileNotFoundException, IOException {
		this.root = root;
		this.profiles = new HashMap<>();
		this.profilesByName = new HashMap<>();
		for (File f : root.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".profile");
			}
		})) {
			addProfile(Profile.load(f), f);
		}
		addProfile(Profile.defaultCar, null);
		addProfile(Profile.defaultTruck, null);
	}

	private void addProfile(Profile p, File f) {
		profiles.put(p, f);
		profilesByName.put(p.getName(), p);
	}

	private void removeProfile(Profile p) {
		profiles.remove(p);
		profilesByName.remove(p.getName());
	}

	/**
	 * Deletes all precalculations ({@link ProfileMapCombination
	 * ProfileMapCombinations} in the {@link ProfileMapManager}) with the same
	 * profile name as {@code profile}.
	 */
	private void deletePrecalculations(Profile profile) {
		final String name = profile.getName();
		for (ProfileMapCombination precalculation : new HashSet<>(
				ProfileMapManager.getInstance().getCombinations())) {
			if (name.equals(precalculation.getProfile().getName())) {
				ProfileMapManager.getInstance().deletePrecalculation(
						precalculation);
			}
		}
	}

	/**
	 * Removes the given profile from the internal list,
	 * {@link ProfileMapManager#deletePrecalculation(ProfileMapCombination)
	 * deletes} all its precalculations and deletes it from disk.
	 * 
	 * @param profile
	 *            The profile that shall be deleted.
	 */
	public void deleteProfile(Profile profile) {
		if (profile.isDefault()) {
			throw new IllegalArgumentException(
					"Can’t delete a default profile!");
		}
		if (!profiles.containsKey(profile)) {
			return;
		}
		profiles.get(profile).delete();
		deletePrecalculations(profile);
		removeProfile(profile);
	}

	/**
	 * Adds the given profile to the internal list and saves it on disk. Any
	 * existing precalculations for a profile of the same name are deleted.
	 * 
	 * @param profile
	 *            The profile that shall be saved.
	 * @throws IOException
	 */
	public void saveProfile(Profile profile) throws IOException {
		if (profile.isDefault()) {
			throw new IllegalArgumentException("Can’t save a default profile!");
		}
		Profile existing = profilesByName.get(profile.getName());
		if (existing != null) {
			if (existing.equals(profile)) {
				return;
			}
			deletePrecalculations(existing);
		}
		File f = profiles.get(existing);
		if (f == null) {
			f = new File(root, profile.getName() + ".profile");
			f.createNewFile();
		}
		profile.save(f);
		addProfile(profile, f);
	}

	/**
	 * Returns all profiles in the internal list.
	 * 
	 * @return All profiles.
	 */
	public Set<Profile> getProfiles() {
		return profiles.keySet();
	}

	/**
	 * Initializes the {@link ProfileManager}.
	 * 
	 * @param rootDirectory
	 *            The directory that contains all profiles.
	 * 
	 * @throws IllegalStateException
	 *             If the ProfileManager is already initialized.
	 */
	public static void init(File rootDirectory) throws IOException {
		if (instance != null) {
			throw new IllegalStateException("Already initialized!");
		}
		instance = new ProfileManager(rootDirectory);
	}

	/**
	 * Returns the {@link ProfileManager} instance. This is only allowed if the
	 * ProfileManager was previously {@link #init(File) initialized}.
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
