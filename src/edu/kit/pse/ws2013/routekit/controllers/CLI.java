package edu.kit.pse.ws2013.routekit.controllers;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.ProgressListener;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;
import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.util.FileUtil;

/**
 * Is the user interface and {@link ProgressListener} for command line
 * interaction.
 */
public class CLI implements ProgressListener, Runnable {
	protected final ManagementActions actions;

	public CLI(String[] args) {
		final MapManager mapManager;
		final ProfileManager profileManager;
		final ProfileMapManager profileMapManager;
		if (args.length == 0) {
			actions = ManagementActions.noActions;
			return;
		} else if (args.length == 1
				&& (args[0].equals("-h") || args[0].equals("--help")
						|| args[0].equals("--usage") || args[0]
							.equals("--version"))) {
			// these options don’t need the ProfileMapManager, skip init
			mapManager = null;
			profileManager = null;
			profileMapManager = null;
		} else {
			try {
				ProfileMapManager.init(FileUtil.getRootDir(), null);
				mapManager = MapManager.getInstance();
				profileManager = ProfileManager.getInstance();
				profileMapManager = ProfileMapManager.getInstance();
			} catch (IOException e) {
				e.printStackTrace();
				actions = ManagementActions.noActions;
				return;
			}
		}
		final Set<FutureMap> newOrUpdatedMaps = new HashSet<>();
		final Set<StreetMap> deletedMaps = new HashSet<>();
		final Set<ProfileMapCombination> deletedPrecalculations = new HashSet<>();
		final Set<ProfileMapCombination> newPrecalculations = new HashSet<>();
		for (int i = 0; i < args.length; i++) {
			final String arg = args[i];
			switch (arg) {
			case "-h":
			case "--help":
			case "--usage":
			case "--version": {
				if (arg.equalsIgnoreCase("--version")) {
					System.out.println("routeKIT version 1.0.0");
				} else {
					for (String line : new String[] {
							"routeKIT: Programm zur Routenplanung und -berechnung.",
							"",
							"Optionen:",
							"",
							"  --help",
							"  --usage",
							"      Gibt diesen Hilfetext aus.",
							"  --version",
							"      Gibt die Version von routeKIT aus.",
							"  --import <Name> <Datei>",
							"  --import-map <Name> <Datei>",
							"      Importiert eine Karte aus einer OSM-Datei.",
							"  --update <Name> <Datei>",
							"  --update-map <Name> <Datei>",
							"      Aktualisiert eine Karte aus einer OSM-Datei.",
							"  --delete-map <Name>",
							"      Löscht eine Karte.",
							"  --delete-precalculation <Kartenname> <Profilname>",
							"      Löscht eine Vorberechnung.",
							"  --precalculate <Kartenname> <Profilname>",
							"      Führt eine Vorberechnung durch.", "",
							"Dieses Programm ist nicht aptitude." }) {
						System.out.println(line);
					}
				}
				actions = ManagementActions.noActions;
				return;
			}
			case "--import":
			case "--import-map":
			case "--update":
			case "--update-map": {
				if (args.length < i + 2) {
					System.err.println("Nicht genug Argumente für Option "
							+ arg + "!");
					actions = ManagementActions.noActions;
					return;
				}
				String name = args[++i];
				String file = args[++i];
				if (!FileUtil.checkMapName(name)) {
					System.err.println("Ungültiger Kartenname: " + name);
					actions = ManagementActions.noActions;
					return;
				}
				if (arg.startsWith("--import")
						&& !mapManager.checkMapName(name)) {
					System.err.println("Kartenname bereits vergeben: " + name);
					actions = ManagementActions.noActions;
					return;
				}
				StreetMap existingMap = null;
				for (StreetMap map : mapManager.getMaps()) {
					if (map.getName().equals(name)) {
						existingMap = map;
						break;
					}
				}
				if (arg.startsWith("--update") && existingMap == null) {
					System.err.println("Keine Karte mit diesem Namen: " + name);
					actions = ManagementActions.noActions;
					return;
				}
				newOrUpdatedMaps.add(new FutureMap(name, new File(file)));
				break;
			}
			case "--delete-map": {
				if (args.length < i + 1) {
					System.err.println("Nicht genug Argumente für Option "
							+ arg + "!");
					actions = ManagementActions.noActions;
					return;
				}
				String name = args[++i];
				StreetMap deletedMap = null;
				for (StreetMap map : mapManager.getMaps()) {
					if (map.getName().equals(name)) {
						deletedMap = map;
						break;
					}
				}
				if (deletedMap == null) {
					System.err.println("Keine Karte mit diesem Namen: " + name);
					actions = ManagementActions.noActions;
					return;
				}
				deletedMaps.add(deletedMap);
				break;
			}
			case "--delete-precalculation": {
				if (args.length < i + 2) {
					System.err.println("Nicht genug Argumente für Option "
							+ arg + "!");
					actions = ManagementActions.noActions;
					return;
				}
				String mapName = args[++i];
				String profileName = args[++i];
				StreetMap map = null;
				for (StreetMap existingMap : mapManager.getMaps()) {
					if (existingMap.getName().equals(mapName)) {
						map = existingMap;
						break;
					}
				}
				if (map == null) {
					System.err.println("Keine Karte mit diesem Namen: "
							+ mapName);
					actions = ManagementActions.noActions;
					return;
				}
				Profile profile = null;
				for (Profile existingProfile : profileManager.getProfiles()) {
					if (existingProfile.getName().equals(profileName)) {
						profile = existingProfile;
						break;
					}
				}
				if (profile == null) {
					System.err.println("Kein Profil mit diesem Namen: "
							+ profileName);
					actions = ManagementActions.noActions;
					return;
				}
				ProfileMapCombination combination = profileMapManager
						.getPrecalculation(profile, map);
				if (combination == null) {
					System.err
							.println("Keine Vorberechnung für diese Kombination: "
									+ mapName + " + " + profileName);
					actions = ManagementActions.noActions;
					return;
				}
				deletedPrecalculations.add(combination);
				break;
			}
			case "--precalculate": {
				if (args.length < i + 2) {
					System.err.println("Nicht genug Argumente für Option "
							+ arg + "!");
					actions = ManagementActions.noActions;
					return;
				}
				String mapName = args[++i];
				String profileName = args[++i];
				StreetMap map = null;
				for (StreetMap existingMap : mapManager.getMaps()) {
					if (existingMap.getName().equals(mapName)) {
						map = existingMap;
						break;
					}
				}
				if (map == null) {
					System.err.println("Keine Karte mit diesem Namen: "
							+ mapName);
					actions = ManagementActions.noActions;
					return;
				}
				Profile profile = null;
				for (Profile existingProfile : profileManager.getProfiles()) {
					if (existingProfile.getName().equals(profileName)) {
						profile = existingProfile;
						break;
					}
				}
				if (profile == null) {
					System.err.println("Kein Profil mit diesem Namen: "
							+ profileName);
					actions = ManagementActions.noActions;
					return;
				}
				ProfileMapCombination combination = profileMapManager
						.getPrecalculation(profile, map);
				if (combination != null) {
					deletedPrecalculations.add(combination);
				}
				newPrecalculations.add(new ProfileMapCombination(map, profile));
				break;
			}
			}
		}
		actions = new ManagementActions(newOrUpdatedMaps, deletedMaps,
				deletedPrecalculations, newPrecalculations);
	}

	@Override
	public void run() {
		if (actions != ManagementActions.noActions) {
			actions.execute(null, new ProgressReporter(this,
					"Führe Änderungen aus"));
		}
	}

	@Override
	public void startRoot(String name) {

	}

	@Override
	public void beginTask(String name) {
		System.out.println(name);
	}

	float last = -1;

	@Override
	public void progress(float progress, String name) {
		if (progress - last > 0.001f) {
			System.out.println(Math.floor(progress * 1000) / 10f + ": " + name);
			last = progress;
		}
	}

	@Override
	public void endTask(String name) {

	}

	@Override
	public void finishRoot(String name) {

	}
}
