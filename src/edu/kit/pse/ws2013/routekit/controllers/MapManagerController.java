package edu.kit.pse.ws2013.routekit.controllers;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
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
		initData();
		currentMap = ProfileMapManager.getInstance().getCurrentCombination()
				.getStreetMap();
		mmv = new MapManagerView(view, this, currentMap,
				precalculations.keySet(), precalculations.get(currentMap));
		mmv.setVisible(true);
	}

	/**
	 * (Re-)Initialize {@link #precalculations}, {@link #profilesByName} and
	 * {@link #mapsByName}.
	 */
	private void initData() {
		precalculations.clear();
		profilesByName.clear();
		mapsByName.clear();
		for (Profile profile : ProfileManager.getInstance().getProfiles()) {
			profilesByName.put(profile.getName(), profile);
		}
		for (StreetMap map : MapManager.getInstance().getMaps()) {
			precalculations.put(map, new HashSet<Profile>());
			mapsByName.put(map.getName(), map);
		}
		for (ProfileMapCombination combination : ProfileMapManager
				.getInstance().getCombinations()) {
			precalculations.get(combination.getStreetMap()).add(
					combination.getProfile());
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
	 * Führt alle vom Benutzer vorgenommenen Änderungen aus. Dazu gehören das
	 * Importieren und Löschen von Karte sowie das Hinzufügen oder Löschen von
	 * Profilen je Karte (Löscht also Vorberechnung oder erzeugt neue).
	 */
	public void saveAllChanges() {
		// TODO implement
		// add precalculations, remove precalculations, load and add FutureMaps,
		// remove maps.
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
			// the ProfileManager can do a lot of stuff;
			// specifically, we need to completely reload precalculations and
			// profileByName.
			initData();
			Set<Profile> profiles = precalculations.get(currentMap);
			profiles.add(addedProfile);
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
		mmv.setCurrentMap(currentMap, precalculations.get(currentMap));
	}

	protected MapManagerView getView() {
		return mmv;
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
