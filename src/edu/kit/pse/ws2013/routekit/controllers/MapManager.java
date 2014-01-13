package edu.kit.pse.ws2013.routekit.controllers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.util.FileUtil;

/**
 * Manages the street maps and saves/loads them to/from disk.
 */
public class MapManager {

	private static MapManager instance;

	private final File root;
	private final Map<StreetMap, File> maps;

	private MapManager(File root) {
		this.root = root;
		this.maps = new HashMap<>();
		if (!root.isDirectory()) {
			throw new IllegalArgumentException(root.toString()
					+ " is not a directory!");
		}
		for (String childString : root.list()) {
			File child = new File(root, childString);
			if (child.isDirectory()) {
				maps.put(StreetMap.loadLazily(child), child);
			}
		}
	}

	/**
	 * Removes the given map from the internal list,
	 * {@link ProfileMapManager#deletePrecalculation(ProfileMapCombination)
	 * deletes} all its precalculations and deletes it from disk.
	 * 
	 * @param map
	 *            The map that shall be deleted.
	 */
	public void deleteMap(StreetMap map) {
		try {
			FileUtil.rmRf(maps.get(map));
		} catch (IOException e) {

		}
		for (ProfileMapCombination precalculation : ProfileMapManager
				.getInstance().getCombinations()) {
			if (map.equals(precalculation.getStreetMap())) {
				ProfileMapManager.getInstance().deletePrecalculation(
						precalculation, false);
			}
		}
		maps.remove(map);
	}

	/**
	 * Adds the given map to the internal list and saves it on disk.
	 * 
	 * @param map
	 *            The map that shall be saved.
	 * @throws IOException
	 */
	public void saveMap(StreetMap map) throws IOException {
		File f = maps.get(map);
		if (f == null) {
			f = new File(root, map.getName());
			f.mkdir();
		}
		map.save(f);
		maps.put(map, f);
	}

	/**
	 * Returns all maps in the internal list.
	 * 
	 * @return All maps.
	 */
	public Set<StreetMap> getMaps() {
		return maps.keySet();
	}

	/**
	 * Initializes the {@link MapManager}.
	 * 
	 * @param rootDirectory
	 *            The directory that contains all maps.
	 * 
	 * @throws IllegalStateException
	 *             If the MapManager is already initialized.
	 */
	public static void init(File rootDirectory) {
		if (instance != null) {
			throw new IllegalStateException("Already initialized!");
		}
		instance = new MapManager(rootDirectory);
	}

	/**
	 * Returns the {@link MapManager} instance. This is only allowed if the
	 * MapManager was previously {@link #init() initialized}.
	 * 
	 * @return The MapManager instance.
	 * @throws IllegalStateException
	 *             If the MapManager is uninitialized.
	 */
	public static MapManager getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Not yet initialized!");
		}
		return instance;
	}
}
