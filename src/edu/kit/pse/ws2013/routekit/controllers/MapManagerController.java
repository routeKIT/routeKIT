package edu.kit.pse.ws2013.routekit.controllers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.xml.sax.SAXException;

import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.precalculation.DummyMapImporter;
import edu.kit.pse.ws2013.routekit.precalculation.PreCalculator;
import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.views.MainView;
import edu.kit.pse.ws2013.routekit.views.MapManagerView;

/**
 * The controller for the {@link MapManagerView}.
 */
public class MapManagerController {
	MapManagerView mmv;

	private final Map<StreetMap, Set<Profile>> precalculations = new HashMap<>();
	private final Map<String, Profile> profilesByName = new HashMap<>();
	private final Map<String, StreetMap> mapsByName = new HashMap<>();
	private StreetMap currentMap;

	public MapManagerController(MainView view) {
		initPrecalculations();
		initNameMaps();
		currentMap = ProfileMapManager.getInstance().getCurrentCombination()
				.getStreetMap();
		Set<Profile> profilesForCurrentMap = precalculations.get(currentMap);
		if (profilesForCurrentMap == null) {
			profilesForCurrentMap = new HashSet<>();
		}
		mmv = new MapManagerView(view, this, currentMap, MapManager
				.getInstance().getMaps(), profilesForCurrentMap);
		mmv.setVisible(true);
	}

	private void initPrecalculations() {
		precalculations.clear();
		for (ProfileMapCombination combination : ProfileMapManager
				.getInstance().getCombinations()) {
			StreetMap map = combination.getStreetMap();
			Set<Profile> profiles = precalculations.get(map);
			if (profiles == null) {
				profiles = new HashSet<>();
			}
			profiles.add(combination.getProfile());
			precalculations.put(map, profiles);
		}
	}

	/**
	 * (Re-)Initialize {@link #profilesByName} and {@link #mapsByName}.
	 */
	private void initNameMaps() {
		initProfilesByName();
		initMapsByName();
	}

	private void initMapsByName() {
		mapsByName.clear();
		for (StreetMap map : MapManager.getInstance().getMaps()) {
			mapsByName.put(map.getName(), map);
		}
	}

	private void initProfilesByName() {
		profilesByName.clear();
		for (Profile profile : ProfileManager.getInstance().getProfiles()) {
			profilesByName.put(profile.getName(), profile);
		}
	}

	/**
	 * Removes the current map.
	 * <p>
	 * (Note that, as with all other changes, this is only propagated to the
	 * application’s model in {@link #saveAllChanges()}.)
	 * <p>
	 * If the current map {@link StreetMap#isDefault() is a default map}, an
	 * {@link IllegalStateException} is thrown.
	 */
	public void deleteCurrentMap() {
		if (currentMap.isDefault()) {
			throw new IllegalStateException("Can’t delete a default map!");
		}
		precalculations.remove(currentMap);
		mapsByName.remove(currentMap.getName());
		Set<StreetMap> availableMaps = precalculations.keySet();
		currentMap = availableMaps.iterator().next(); // TODO use previous map
		mmv.setAvailableMaps(availableMaps);
		mmv.setCurrentMap(currentMap, precalculations.get(currentMap));
	}

	/**
	 * Removes the given profile from the current map.
	 * 
	 * @param profile
	 *            The profile that shall be removed.
	 */
	public void removeProfile(String profileName) {
		Profile removedProfile = profilesByName.get(profileName);
		if (removedProfile == null) {
			throw new IllegalArgumentException("Profile with name '"
					+ removedProfile + "' does not exist!");
		}
		Set<Profile> profiles = precalculations.get(currentMap);
		profiles.remove(removedProfile);
		mmv.setCurrentMap(currentMap, profiles);
	}

	/**
	 * Performs all changes that the user requested.
	 * <ul>
	 * <li>imports new and changed maps</li>
	 * <li>deletes removed maps</li>
	 * <li>deletes removed precalculations</li>
	 * <li>performs requested precalculations</li>
	 * </ul>
	 */
	public void saveAllChanges() {
		// TODO all of this should NOT run in the UI thread
		MapManager mapManager = MapManager.getInstance();
		ProfileMapManager profileMapManager = ProfileMapManager.getInstance();
		final MapManagementDiff diff = getChanges();
		for (StreetMap map : diff.getDeletedMaps()) {
			mapManager.deleteMap(map);
		}
		for (ProfileMapCombination precalculation : diff
				.getDeletedPrecalculations()) {
			profileMapManager.deletePrecalculation(precalculation);
		}
		DummyMapImporter importer = new DummyMapImporter();
		for (FutureMap map : diff.getNewOrUpdatedMaps()) {
			try {
				mapManager.saveMap(importer.importMap(map.getOsmFile(),
						map.getName()));
			} catch (IOException | SAXException e) {
				// TODO View should display this error
				e.printStackTrace();
			}
		}
		PreCalculator calculator = new PreCalculator();
		for (ProfileMapCombination combination : diff.getNewPrecalculations()) {
			calculator.doPrecalculation(combination);
			profileMapManager.save(combination);
		}
	}

	/**
	 * Opens a Profile Management Dialog to select a profile to add to the list
	 * of precalculations for the current map.
	 * 
	 * @see ProfileManagerController
	 */
	public void addProfile() {
		ProfileManagerController c = new ProfileManagerController(mmv);
		Profile addedProfile = c.getSelectedProfile();
		if (addedProfile != null) {
			// the ProfileManager can delete precalculations and add profiles
			initProfilesByName();
			for (ProfileMapCombination deletedCombination : c
					.getDeletedPrecalculations()) {
				precalculations.get(deletedCombination.getStreetMap()).remove(
						deletedCombination.getProfile());
			}
			Set<Profile> profiles = precalculations.get(currentMap);
			if (profiles == null) {
				profiles = new HashSet<>();
			}
			profiles.add(addedProfile);
			precalculations.put(currentMap, profiles);
			mmv.setCurrentMap(currentMap, profiles);
		}
	}

	/**
	 * Adds a new map with the given name, remembers to load it from the given
	 * file in {@link #saveAllChanges()}, and selects it.
	 * <p>
	 * Note that the two actions “Import” and “Update”, which are separate in
	 * the GUI, are both performed through this method. For the first action,
	 * the GUI must ensure that the name is unused, while for the second action
	 * an existing name is given.
	 * 
	 * @param name
	 *            The name of the new map.
	 * @param file
	 *            The file from which it should be loaded (later).
	 */
	public void importMap(String name, File file) {
		currentMap = new FutureMap(name, file);
		mapsByName.put(name, currentMap);
		precalculations.put(currentMap, new HashSet<Profile>());
		mmv.setAvailableMaps(precalculations.keySet());
		mmv.setCurrentMap(currentMap, precalculations.get(currentMap));
	}

	/**
	 * Switch to the map with the given name.
	 * 
	 * @param map
	 *            The name of the new map.
	 */
	public void changeMap(String mapName) {
		StreetMap newMap = mapsByName.get(mapName);
		if (newMap == null) {
			throw new IllegalArgumentException("StreetMap with name '"
					+ mapName + "' does not exist!");
		}
		currentMap = newMap;
		Set<Profile> profiles = precalculations.get(currentMap);
		if (profiles == null) {
			profiles = new HashSet<>();
		}
		mmv.setCurrentMap(currentMap, profiles);
	}

	protected MapManagerView getView() {
		return mmv;
	}

	/**
	 * Gets all changes that the user requested, that is, the changes that
	 * {@link #saveAllChanges()} would execute if called right now.
	 * 
	 * @return All changes.
	 */
	public MapManagementDiff getChanges() {
		MapManager mapManager = MapManager.getInstance();
		ProfileMapManager profileMapManager = ProfileMapManager.getInstance();
		return MapManagementDiff.diff(mapManager.getMaps(),
				profileMapManager.getCombinations(), precalculations);
	}

	public static class MapManagementDiff {
		/**
		 * Maps that need to be imported.
		 */
		private final Set<FutureMap> newOrUpdatedMaps;
		/**
		 * Maps that need to be deleted.
		 * <p>
		 * (Does <i>not</i> contain updated maps – if you want to know which
		 * precalculations to delete, use {@link #deletedPrecalculations}.)
		 */
		private final Set<StreetMap> deletedMaps;
		/**
		 * Precalculations that need to be deleted.
		 */
		private final Set<ProfileMapCombination> deletedPrecalculations;
		/**
		 * Precalculations that need to be performed.
		 */
		private final Set<ProfileMapCombination> newPrecalculations;

		public MapManagementDiff(Set<FutureMap> newOrUpdatedMaps,
				Set<StreetMap> deletedMaps,
				Set<ProfileMapCombination> deletedPrecalculations,
				Set<ProfileMapCombination> newPrecalculations) {
			this.newOrUpdatedMaps = newOrUpdatedMaps;
			this.deletedMaps = deletedMaps;
			this.deletedPrecalculations = deletedPrecalculations;
			this.newPrecalculations = newPrecalculations;
		}

		/**
		 * @return Maps that need to be imported.
		 */
		public Set<FutureMap> getNewOrUpdatedMaps() {
			return newOrUpdatedMaps;
		}

		/**
		 * @return Maps that need to be deleted.
		 *         <p>
		 *         (Does <i>not</i> contain updated maps – if you want to know
		 *         which precalculations to delete, use
		 *         {@link #deletedPrecalculations}.
		 */
		public Set<StreetMap> getDeletedMaps() {
			return deletedMaps;
		}

		/**
		 * @return Precalculations that need to be deleted.
		 */
		public Set<ProfileMapCombination> getDeletedPrecalculations() {
			return deletedPrecalculations;
		}

		/**
		 * @return Precalculations that need to be performed.
		 */
		public Set<ProfileMapCombination> getNewPrecalculations() {
			return newPrecalculations;
		}

		static MapManagementDiff diff(Set<StreetMap> maps,
				Set<ProfileMapCombination> precalculations,
				Map<StreetMap, Set<Profile>> changes) {
			final Set<FutureMap> newOrUpdatedMaps;
			final Set<StreetMap> deletedMaps;
			final Set<ProfileMapCombination> deletedPrecalculations;
			final Set<ProfileMapCombination> newPrecalculations;

			deletedMaps = new HashSet<>();
			for (StreetMap map : maps) {
				if (!changes.containsKey(map)) {
					deletedMaps.add(map);
				}
			}

			newOrUpdatedMaps = new HashSet<>();
			final Map<String, StreetMap> mapsByName = new HashMap<>();
			for (StreetMap map : maps) {
				mapsByName.put(map.getName(), map);
			}
			for (StreetMap map : changes.keySet()) {
				StreetMap existing = mapsByName.get(map.getName());
				if (existing == null || map != existing) {
					assert (map instanceof FutureMap);
					newOrUpdatedMaps.add((FutureMap) map);
				}
			}

			deletedPrecalculations = new HashSet<>();
			for (ProfileMapCombination precalculation : precalculations) {
				StreetMap map = precalculation.getStreetMap();
				if (deletedMaps.contains(map)
						|| newOrUpdatedMaps.contains(map)
						|| !changes.get(map).contains(
								precalculation.getProfile())) {
					deletedPrecalculations.add(precalculation);
				}
			}

			newPrecalculations = new HashSet<>();
			final Map<StreetMap, Set<Profile>> precalculationsAsMap = new HashMap<>();
			for (ProfileMapCombination precalculation : precalculations) {
				StreetMap map = precalculation.getStreetMap();
				Set<Profile> profiles = precalculationsAsMap.get(map);
				if (profiles == null) {
					profiles = new HashSet<>();
				}
				profiles.add(precalculation.getProfile());
				precalculationsAsMap.put(map, profiles);
			}
			for (Entry<StreetMap, Set<Profile>> combinationSet : changes
					.entrySet()) {
				StreetMap map = combinationSet.getKey();
				for (Profile profile : combinationSet.getValue()) {
					Set<Profile> profiles = precalculationsAsMap.get(map);
					if (profiles == null) {
						profiles = new HashSet<>();
					}
					if (newOrUpdatedMaps.contains(map)
							|| !profiles.contains(profile)) {
						newPrecalculations.add(new ProfileMapCombination(map,
								profile));
					}
				}
			}

			return new MapManagementDiff(newOrUpdatedMaps, deletedMaps,
					deletedPrecalculations, newPrecalculations);
		}
	}
}

class FutureMap extends StreetMap {

	private final File osmFile;

	public FutureMap(String name, File osmFile) {
		super(null, null);
		this.name = name;
		this.osmFile = osmFile;
	}

	@Override
	public boolean isDefault() {
		return false;
	}

	public File getOsmFile() {
		return osmFile;
	}
}