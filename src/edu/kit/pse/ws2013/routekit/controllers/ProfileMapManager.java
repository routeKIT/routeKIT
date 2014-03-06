package edu.kit.pse.ws2013.routekit.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
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
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;
import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.util.Dummies;
import edu.kit.pse.ws2013.routekit.util.FileUtil;

/**
 * The {@link ProfileMapManager} manages {@link ProfileMapCombination
 * ProfileMapCombinations}. It loads them from the disk when {@link #init(File)
 * initialized} and reads the information which one is the current combination.
 * 
 * @author Lucas Werkmeister
 */
public class ProfileMapManager {

	private static final Charset INDEX_FILE_CHARSET = Charset.forName("UTF-8");

	private static ProfileMapManager instance = null;

	private final File root;
	private ProfileMapCombination current;
	private final Set<ProfileMapCombination> precalculations;
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
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(indexFile), INDEX_FILE_CHARSET))) {
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
		this.precalculations = new HashSet<>();
		for (Entry<String, Set<String>> entry : combinations.entrySet()) {
			String mapName = entry.getKey();
			StreetMap map = mapsByName.get(mapName);
			if (map == null) {
				System.err.println("Map '" + mapName + "' not found, skipping");
				continue;
			}
			for (String profileName : entry.getValue()) {
				Profile profile = profilesByName.get(profileName);
				if (profile == null) {
					System.err.println("Profile '" + profileName
							+ "' not found, skipping");
					continue;
				}
				ProfileMapCombination combination;
				try {
					combination = ProfileMapCombination
							.loadLazily(profile, map, new File(new File(root,
									mapName), profileName));
				} catch (IOException | IllegalArgumentException e) {
					if (current != null && current.getKey().equals(mapName)
							&& current.getValue().equals(profileName)) {
						// the current combination may not have been
						// precalculated
						combination = new ProfileMapCombination(map, profile);
						this.current = combination;
						continue;
					} else {
						throw e;
					}
				}
				this.precalculations.add(combination);
				if (current != null && mapName.equals(current.getKey())
						&& profileName.equals(current.getValue())) {
					this.current = combination;
				}
			}
		}
		if (current == null || this.current == null) {
			// choose any precalculation
			if (!this.precalculations.isEmpty()) {
				this.current = this.precalculations.iterator().next();
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

	public void savePrecalculation(ProfileMapCombination precalculation) {
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
			return; // don’t write an invalid index file
		}
		precalculations.add(precalculation);
		// 2. write a new index file
		try {
			rewriteIndex();
		} catch (IOException e) {
			e.printStackTrace();
			// don’t return – not critical
		}
		// 3. special case if that was the current combination
		if (precalculation.getProfile().equals(current.getProfile())
				&& precalculation.getStreetMap().equals(current.getStreetMap())) {
			// 3.1 remove the old one from combinations
			if (current != precalculation) {
				precalculations.remove(current);
			}
			// 3.2 update current
			current = precalculation;
			// 3.3 notify listeners
			for (CurrentCombinationListener listener : listeners) {
				listener.currentCombinationChanged(precalculation);
			}
		}
	}

	/**
	 * Sets the current combination to the given one.
	 * <p>
	 * The index file is rewritten, but for performance reasons, the
	 * precalculation (if it’s a precalculation) isn’t saved; if you’re not sure
	 * if the precalculation has been saved, use
	 * {@link #savePrecalculation(ProfileMapCombination)}.
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
		try {
			rewriteIndex();
		} catch (IOException e) {
			e.printStackTrace();
			// don’t return – not critical
		}
		for (CurrentCombinationListener listener : listeners) {
			listener.currentCombinationChanged(combination);
		}
	}

	void rewriteIndex() throws IOException {
		Map<StreetMap, Set<ProfileMapCombination>> combinationsByMap = new HashMap<>();
		for (ProfileMapCombination combo : precalculations) {
			Set<ProfileMapCombination> combos = combinationsByMap.get(combo
					.getStreetMap());
			if (combos == null) {
				combos = new HashSet<>();
			}
			combos.add(combo);
			combinationsByMap.put(combo.getStreetMap(), combos);
		}
		Set<ProfileMapCombination> currentMapCombos = combinationsByMap
				.get(current.getStreetMap());
		if (currentMapCombos == null) {
			currentMapCombos = new HashSet<>();
		}
		currentMapCombos.add(current);
		combinationsByMap.put(current.getStreetMap(), currentMapCombos);
		for (StreetMap map : MapManager.getInstance().getMaps()) {
			if (!combinationsByMap.containsKey(map)) {
				// maps with no precalculations should still be in the index
				combinationsByMap
						.put(map, new HashSet<ProfileMapCombination>());
			}
		}
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(new File(root, "routeKIT.idx")),
				INDEX_FILE_CHARSET))) {
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
		for (ProfileMapCombination combination : precalculations) {
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
	 *            i.&nbsp;e. an element of {@link #getPrecalculations()} – and
	 *            not just any {@link ProfileMapCombination}.)
	 * @param deleteFromDisk
	 *            If {@code true}, delete from disk as well. You usually want to
	 *            do this; the only case where you don’t need this is if you’re
	 *            deleting a precalculation because you’re deleting its
	 *            {@link StreetMap}, in which case the recursive delete of the
	 *            Map’s folder will delete all precalculations as well.
	 */
	public void deletePrecalculation(ProfileMapCombination precalculation,
			boolean deleteFromDisk) {
		if (!precalculations.contains(precalculation)
				&& precalculation != current) {
			throw new IllegalArgumentException("Unknown precalculation!");
		}
		precalculations.remove(precalculation);
		if (deleteFromDisk) {
			try {
				FileUtil.rmRf(new File(new File(root, precalculation
						.getStreetMap().getName()), precalculation.getProfile()
						.getName()));
			} catch (IOException e1) {
				e1.printStackTrace();
				// don’t return – not critical
			}
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

	public Set<ProfileMapCombination> getPrecalculations() {
		return Collections.unmodifiableSet(precalculations);
	}

	public static ProfileMapCombination init(File rootDirectory,
			ProgressReporter pr) throws IOException {
		pr.setSubTasks(new float[] { .1f, .1f, .1f, .7f });
		if (!rootDirectory.exists()) {
			// initFirstStart(rootDirectory);
			rootDirectory.mkdir();
			Dummies.downloadInstall(rootDirectory);
		} else if (!rootDirectory.isDirectory()) {
			throw new IllegalArgumentException(rootDirectory.toString()
					+ " is not a directory!");
		}
		if (instance != null) {
			throw new IllegalStateException("Already initialized!");
		}
		pr.pushTask("Initialisiere ProfileManager");
		ProfileManager.init(rootDirectory);
		pr.nextTask("Initialisiere MapManager");
		MapManager.init(rootDirectory);
		pr.nextTask("Erstelle ProfileMapManager");
		instance = new ProfileMapManager(rootDirectory);
		pr.nextTask("Lade Vorberechnung");
		// un-lazy
		instance.getCurrentCombination().ensureLoaded(pr);
		pr.popTask();
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
