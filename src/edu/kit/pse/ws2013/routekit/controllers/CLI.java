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
	protected ManagementActions actions;

	public CLI(String[] args) {
		try {
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
								"  --select <Kartenname> <Profilname>",
								"      Wählt eine Kombination aus.",
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
					checkOptionCount(args, i, arg, 2);
					String name = args[++i];
					String file = args[++i];
					if (!FileUtil.checkMapName(name)) {
						System.err.println("Ungültiger Kartenname: " + name);
						actions = ManagementActions.noActions;
						throw new Error();
					}
					StreetMap existingMap = findMap(mapManager, name,
							arg.startsWith("--update"));
					if (arg.startsWith("--import") && existingMap != null) {
						System.err.println("Kartenname bereits vergeben: "
								+ name);
						actions = ManagementActions.noActions;
						throw new Error();
					}
					newOrUpdatedMaps.add(new FutureMap(name, new File(file)));
					break;
				}
				case "--select": {
					checkOptionCount(args, i, arg, 2);
					String mapName = args[++i];
					String profileName = args[++i];
					StreetMap map = findMap(mapManager, mapName, true);
					Profile profile = findProfile(profileManager, profileName);
					ProfileMapCombination combination = profileMapManager
							.getPrecalculation(profile, map);
					ProfileMapManager.getInstance().setCurrentCombination(
							combination);
					break;
				}
				case "--delete-map": {
					checkOptionCount(args, i, arg, 1);
					String name = args[++i];
					StreetMap deletedMap = null;
					deletedMap = findMap(mapManager, name, true);
					deletedMaps.add(deletedMap);
					break;
				}
				case "--delete-precalculation": {
					checkOptionCount(args, i, arg, 2);
					String mapName = args[++i];
					String profileName = args[++i];
					StreetMap map = findMap(mapManager, mapName, true);
					Profile profile = findProfile(profileManager, profileName);
					ProfileMapCombination combination = profileMapManager
							.getPrecalculation(profile, map);
					if (combination == null) {
						System.err
								.println("Keine Vorberechnung für diese Kombination: "
										+ mapName + " + " + profileName);
						actions = ManagementActions.noActions;
						throw new Error();
					}
					deletedPrecalculations.add(combination);
					break;
				}
				case "--precalculate": {
					checkOptionCount(args, i, arg, 2);
					String mapName = args[++i];
					String profileName = args[++i];
					StreetMap map = findMap(mapManager, mapName, true);
					Profile profile = findProfile(profileManager, profileName);
					ProfileMapCombination combination = profileMapManager
							.getPrecalculation(profile, map);
					if (combination != null) {
						deletedPrecalculations.add(combination);
					}
					newPrecalculations.add(new ProfileMapCombination(map,
							profile));
					break;
				}
				}
			}
			actions = new ManagementActions(newOrUpdatedMaps, deletedMaps,
					deletedPrecalculations, newPrecalculations);
		} catch (Error r) {
			// No-op, syntax error
		}
	}

	private void checkOptionCount(String[] args, int i, final String arg,
			int needed) throws Error {
		if (args.length <= i + needed) {
			System.err.println("Nicht genug Argumente für Option " + arg + "!");
			actions = ManagementActions.noActions;
			throw new Error();
		}
	}

	private Profile findProfile(final ProfileManager profileManager,
			String profileName) throws Error {
		Profile profile = null;
		for (Profile existingProfile : profileManager.getProfiles()) {
			if (existingProfile.getName().equalsIgnoreCase(profileName)) {
				profile = existingProfile;
				break;
			}
		}
		if (profile == null) {
			System.err.println("Kein Profil mit diesem Namen: " + profileName);
			actions = ManagementActions.noActions;
			throw new Error();
		}
		return profile;
	}

	private StreetMap findMap(final MapManager mapManager, String mapName,
			boolean require) throws Error {
		StreetMap map = null;
		for (StreetMap existingMap : mapManager.getMaps()) {
			if (existingMap.getName().equalsIgnoreCase(mapName)) {
				map = existingMap;
				break;
			}
		}
		if (map == null && require) {
			System.err.println("Keine Karte mit diesem Namen: " + mapName);
			actions = ManagementActions.noActions;
			throw new Error();
		}
		return map;
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
