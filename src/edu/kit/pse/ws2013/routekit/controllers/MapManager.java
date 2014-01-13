package edu.kit.pse.ws2013.routekit.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.map.StreetMap;

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
	 * Removes the given map from the internal list and deletes it from disk.
	 * 
	 * @param map
	 *            The map that shall be deleted.
	 */
	public void deleteMap(StreetMap map) {
		try {
			// recursive directory delete:
			// http://stackoverflow.com/a/8685959/1420237
			Files.walkFileTree(maps.get(map).toPath(),
					new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file,
								BasicFileAttributes attrs) throws IOException {
							Files.delete(file);
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFileFailed(Path file,
								IOException exc) throws IOException {
							Files.delete(file);
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult postVisitDirectory(Path dir,
								IOException exc) throws IOException {
							if (exc == null) {
								Files.delete(dir);
								return FileVisitResult.CONTINUE;
							} else {
								throw exc;
							}
						}
					});
		} catch (IOException e) {

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
