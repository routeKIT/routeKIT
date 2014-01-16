package edu.kit.pse.ws2013.routekit.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.models.CurrentCombinationListener;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.util.FileUtil;

/**
 * The {@link ProfileMapManager} manages {@link ProfileMapCombination
 * ProfileMapCombinations}. It loads them from the disk when {@link #init(File)
 * initialized} and reads the information which one is the current combination.
 * 
 * @author Lucas Werkmeister
 */
public class ProfileMapManager {

	private static ProfileMapManager instance = null;

	private final File root;
	private ProfileMapCombination current;
	private final Set<ProfileMapCombination> combinations;
	private final Set<CurrentCombinationListener> listeners = new HashSet<>();

	private ProfileMapManager(File root) throws IOException {
		this.root = root;
		if (!root.isDirectory()) {
			throw new IllegalArgumentException(root.toString()
					+ " is not a directory!");
		}
		final File indexFile = new File(root, "routeKIT.idx");
		Map<String, Set<String>> combinations = new HashMap<>();
		Entry<String, String> current = null;
		try (BufferedReader br = new BufferedReader(new FileReader(indexFile))) {
			String currentMap = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.charAt(0) == '\t') {
					// a profile
					if (currentMap == null) {
						throw new IOException(
								"No current map when reading profile '"
										+ line.substring(1) + "'!");
					}
					final String profile;
					final boolean isDefault;
					if (line.startsWith("\t* ")) {
						profile = line.substring("\t* ".length());
						isDefault = true;
					} else {
						profile = line.substring(1);
						isDefault = false;
					}
					combinations.get(currentMap).add(profile);
					if (isDefault) {
						Entry<String, String> newCurrent = new AbstractMap.SimpleEntry<>(
								currentMap, profile);
						if (current != null) {
							throw new IOException("Two current combinations – "
									+ current + " and " + newCurrent + "!");
						}
						current = newCurrent;
					}
				} else {
					// a map
					currentMap = line;
					combinations.put(currentMap, new HashSet<String>());
				}
			}
		}
		if (current == null) {
			System.err
					.println("No current combination found, will choose arbitrary one!"); // TODO
		}
		final Map<String, Profile> profilesByName = new HashMap<>();
		for (Profile p : ProfileManager.getInstance().getProfiles()) {
			profilesByName.put(p.getName(), p);
		}
		final Map<String, StreetMap> mapsByName = new HashMap<>();
		for (StreetMap m : MapManager.getInstance().getMaps()) {
			mapsByName.put(m.getName(), m);
		}
		this.combinations = new HashSet<>();
		for (Entry<String, Set<String>> entry : combinations.entrySet()) {
			String mapName = entry.getKey();
			StreetMap map = mapsByName.get(mapName);
			for (String profileName : entry.getValue()) {
				Profile profile = profilesByName.get(profileName);
				ProfileMapCombination combination = ProfileMapCombination.load(
						profile, map, new File(new File(root, mapName),
								profileName));
				this.combinations.add(combination);
				if (current != null && mapName.equals(current.getKey())
						&& profileName.equals(current.getValue())) {
					this.current = combination;
				}
			}
		}
		if (current == null) {
			// choose any precalculation
			if (!this.combinations.isEmpty()) {
				this.current = this.combinations.iterator().next();
			} else {
				// there are no precalculations
				if (!mapsByName.isEmpty()) {
					selectProfileAndMap(Profile.defaultCar, mapsByName.values()
							.iterator().next());
				} else {
					// TODO disallow this
					// we’ll allow it for now for Dummies
				}
			}
		}
	}

	public ProfileMapCombination getCurrentCombination() {
		return current;
	}

	public void addCurrentCombinationListener(
			CurrentCombinationListener listener) {
		listeners.add(listener);
	}

	public void save(ProfileMapCombination precalculation) {
		if (!precalculation.isCalculated()) {
			throw new IllegalArgumentException("Not a precalculation!");
		}
		// 1. save the precalculation
		try {
			precalculation.save(new File(new File(root, precalculation
					.getStreetMap().getName()), precalculation.getProfile()
					.getName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 2. write a new index file
		try {
			rewriteIndex();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the current combination to the given one.
	 * <p>
	 * If the given combination is not
	 * {@link ProfileMapCombination#isCalculated() precalculated}, then it is
	 * only remembered as the "current" one, and forgotten as soon as this
	 * method is called with another combination; if it is precalculated, it’s
	 * stored in the internal list of {@link ProfileMapCombination
	 * ProfileMapCombinations} and {@link #save(ProfileMapCombination) saved} to
	 * disk (it’s assumed that the profile and the map are already saved).
	 * <p>
	 * Note that you’d usually want to use
	 * {@link #selectProfileAndMap(Profile, StreetMap)} instead of this method,
	 * since that method looks for an existing (precalculated) combination.
	 * Especially, you should <i>never</i> call <code>
	 * setCurrentCombination(new ProfileMapCombination(map, profile));
	 * </code>
	 * 
	 * @param combination
	 *            The combination.
	 */
	public void setCurrentCombination(ProfileMapCombination combination) {
		if (combination == null) {
			throw new IllegalArgumentException("combination must not be null!");
		}
		current = combination;
		if (current.isCalculated()) {
			combinations.add(combination);
			save(combination);
		}
		for (CurrentCombinationListener listener : listeners) {
			listener.currentCombinationChanged(combination);
		}
	}

	private void rewriteIndex() throws IOException {
		Map<StreetMap, Set<ProfileMapCombination>> combinationsByMap = new HashMap<>();
		for (ProfileMapCombination combo : combinations) {
			Set<ProfileMapCombination> combos = combinationsByMap.get(combo
					.getStreetMap());
			if (combos == null) {
				combos = new HashSet<>();
			}
			combos.add(combo);
			combinationsByMap.put(combo.getStreetMap(), combos);
		}
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				root, "routeKIT.idx")))) {
			for (Entry<StreetMap, Set<ProfileMapCombination>> map : combinationsByMap
					.entrySet()) {
				bw.write(map.getKey().getName());
				bw.newLine();
				for (ProfileMapCombination combo : map.getValue()) {
					bw.write("\t");
					if (combo == current) {
						bw.write("* ");
					}
					bw.write(combo.getProfile().getName());
					bw.newLine();
				}
			}
		}
	}

	/**
	 * Searches for a precalculation with the given profile and map. If the is
	 * already pre-calculated, the {@link ProfileMapCombination} is returned;
	 * otherwise, {@code null} is returned.
	 * 
	 * @param profile
	 *            The profile.
	 * @param map
	 *            The map.
	 * @return A {@link ProfileMapCombination} with the results of a
	 *         precalculation for the given profile and map if it exists,
	 *         otherwise {@code null}.
	 */
	public ProfileMapCombination getPrecalculation(Profile profile,
			StreetMap map) {
		for (ProfileMapCombination combination : combinations) {
			if (combination.getProfile().equals(profile)
					&& combination.getStreetMap().equals(map)) {
				return combination;
			}
		}
		return null;
	}

	/**
	 * Selects the given profile and map. If a precalculation exists, it becomes
	 * the current one and is returned; if it doesn’t, then a new
	 * {@link ProfileMapCombination} is created and selected (but not saved).
	 * <p>
	 * In other words, this behaves like <code>
	 * setCurrentCombination(getPrecalculation(profile, map) else new ProfileMapCombination(map, profile));
	 * </code>
	 * 
	 * @param profile
	 *            The profile.
	 * @param map
	 *            The map.
	 * @return A {@link ProfileMapCombination combination} of the given profile
	 *         and map.
	 */
	public ProfileMapCombination selectProfileAndMap(Profile profile,
			StreetMap map) {
		ProfileMapCombination combination = getPrecalculation(profile, map);
		if (combination == null) {
			combination = new ProfileMapCombination(map, profile);
		}
		setCurrentCombination(combination);
		return combination;
	}

	/**
	 * Remove a precalculation from the internal list and optionally delete it
	 * from disk.
	 * 
	 * @param precalculation
	 *            The precalculation. (This must be an actual precalculation –
	 *            i.&nbsp;e. an element of {@link #getCombinations()} – and not
	 *            just any {@link ProfileMapCombination}.)
	 * @param deleteFromDisk
	 *            If {@code true}, delete from disk as well. You usually want to
	 *            do this; the only case where you don’t need this is if you’re
	 *            deleting a precalculation because you’re deleting it’s
	 *            {@link StreetMap}, in which case the recursive delete of the
	 *            Map’s folder will delete all precalculations as well.
	 */
	public void deletePrecalculation(ProfileMapCombination precalculation,
			boolean deleteFromDisk) {
		if (!precalculation.isCalculated()) {
			throw new IllegalArgumentException(
					"Can’t delete a not precalculated ProfileMapCombination!");
		}
		if (!combinations.contains(precalculation)) {
			throw new IllegalArgumentException("Unknown precalculation!");
		}
		combinations.remove(precalculation);
		try {
			FileUtil.rmRf(new File(new File(root, precalculation.getStreetMap()
					.getName()), precalculation.getProfile().getName()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			rewriteIndex();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove a precalculation from the internal list and delete it from disk.
	 * 
	 * @param precalculation
	 *            The precalculation.
	 * @see #deletePrecalculation(ProfileMapCombination, boolean)
	 */
	public void deletePrecalculation(ProfileMapCombination precalculation) {
		deletePrecalculation(precalculation, true);
	}

	public Set<ProfileMapCombination> getCombinations() {
		return Collections.unmodifiableSet(combinations);
	}

	public static ProfileMapCombination init(File rootDirectory)
			throws IOException {
		if (!rootDirectory.exists()) {
			initFirstStart(rootDirectory);
		} else if (!rootDirectory.isDirectory()) {
			throw new IllegalArgumentException(rootDirectory.toString()
					+ " is not a directory!");
		}
		if (instance != null) {
			throw new IllegalStateException("Already initialized!");
		}
		ProfileManager.init(rootDirectory);
		MapManager.init(rootDirectory);
		instance = new ProfileMapManager(rootDirectory);
		return instance.getCurrentCombination();
	}

	/**
	 * create the root directory and add an empty index file
	 * 
	 * @throws IOException
	 */
	private static void initFirstStart(File rootDirectory) throws IOException {
		rootDirectory.mkdir();
		new File(rootDirectory, "routeKIT.idx").createNewFile();
	}

	public static ProfileMapManager getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Not yet initialized!");
		}
		return instance;
	}
}
