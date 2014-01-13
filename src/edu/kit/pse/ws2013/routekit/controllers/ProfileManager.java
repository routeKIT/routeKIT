package edu.kit.pse.ws2013.routekit.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * Manages the profiles and saves/loads them to/from disk.
 */
public class ProfileManager {

	private static ProfileManager instance;

	private final File root;
	private final Map<Profile, File> profiles;

	private ProfileManager(File root) throws FileNotFoundException, IOException {
		this.root = root;
		this.profiles = new HashMap<>();
		for (File f : root.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".profile");
			}
		})) {
			profiles.put(Profile.load(f), f);
		}
	}

	/**
	 * Removes the given profile from the internal list and deletes it from
	 * disk.
	 * 
	 * @param profile
	 *            The profile that shall be deleted.
	 */
	public void deleteProfile(Profile profile) {
		profiles.get(profile).delete();
		profiles.remove(profile);
	}

	/**
	 * Adds the given profile to the internal list and saves it on disk.
	 * 
	 * @param profile
	 *            The profile that shall be saved.
	 * @throws IOException
	 */
	public void saveProfile(Profile profile) throws IOException {
		File f = profiles.get(profile);
		if (f == null) {
			f = new File(root, profile.getName() + ".profile");
			f.createNewFile();
		}
		profile.save(f);
		profiles.put(profile, f);
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
	 * ProfileManager was previously {@link #init(Set) initialized}.
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
