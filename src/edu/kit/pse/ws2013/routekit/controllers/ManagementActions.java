package edu.kit.pse.ws2013.routekit.controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.xml.sax.SAXException;

import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter.CloseableTask;
import edu.kit.pse.ws2013.routekit.precalculation.MapImporter;
import edu.kit.pse.ws2013.routekit.precalculation.OSMMapImporter;
import edu.kit.pse.ws2013.routekit.precalculation.PreCalculator;

/**
 * A collection of actions when managing maps and precalculations
 */
public class ManagementActions {

	public static final ManagementActions noActions = new ManagementActions(
			Collections.<FutureMap> emptySet(),
			Collections.<StreetMap> emptySet(),
			Collections.<ProfileMapCombination> emptySet(),
			Collections.<ProfileMapCombination> emptySet());

	private final static float FACTOR_DELETE_PRECALCULATION = 1;
	private final static float FACTOR_DELETE_MAP = 1;
	private final static float FACTOR_IMPORT_MAP = 100;
	private final static float FACTOR_PERFORM_PRECALCULATION = 500;

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

	public ManagementActions(Set<FutureMap> newOrUpdatedMaps,
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
	 *         (Does <i>not</i> contain updated maps – if you want to know which
	 *         precalculations to delete, use {@link #deletedPrecalculations}.
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

	/**
	 * Execute the changes.
	 * 
	 * @param selectedMap
	 *            A special map to look for; when a map with this name is
	 *            imported/updated, the updated map is remembered and later
	 *            returned.
	 * @param reporter
	 *            A {@link ProgressReporter} to report progress to.
	 * @return The last imported/updated map with {@code selectedMap}’s name, or
	 *         {@code selectedMap} if no such map was updated/imported.
	 */
	public StreetMap execute(StreetMap selectedMap, ProgressReporter reporter) {
		MapManager mapManager = MapManager.getInstance();
		ProfileMapManager profileMapManager = ProfileMapManager.getInstance();
		final float delPrecalcWeight = getDeletedPrecalculations().size()
				* FACTOR_DELETE_PRECALCULATION;
		final float delMapWeight = getDeletedMaps().size() * FACTOR_DELETE_MAP;
		final float importWeight = getNewOrUpdatedMaps().size()
				* FACTOR_IMPORT_MAP;
		final float precalcWeight = getNewPrecalculations().size()
				* FACTOR_PERFORM_PRECALCULATION;
		final float totalWeight = delPrecalcWeight + delMapWeight
				+ importWeight + precalcWeight;
		reporter.setSubTasks(new float[] { delPrecalcWeight / totalWeight,
				delMapWeight / totalWeight, importWeight / totalWeight,
				precalcWeight / totalWeight });
		reporter.pushTask("Lösche Vorberechnungen");
		reporter.setSubTasks(getDeletedPrecalculations().size());
		for (ProfileMapCombination precalculation : getDeletedPrecalculations()) {
			reporter.pushTask("Lösche Vorberechnung '" + precalculation + "'");
			profileMapManager.deletePrecalculation(precalculation,
					!getDeletedMaps().contains(precalculation.getStreetMap()));
			reporter.popTask();
		}
		reporter.nextTask("Lösche Karten");
		reporter.setSubTasks(getDeletedMaps().size());
		for (StreetMap map : getDeletedMaps()) {
			reporter.pushTask("Lösche Karte '" + map.getName() + "'");
			mapManager.deleteMap(map);
			reporter.popTask();
		}
		reporter.popTask();
		MapImporter importer = new OSMMapImporter();
		reporter.pushTask("Importiere und speichere Karten");
		reporter.setSubTasks(getNewOrUpdatedMaps().size());
		/**
		 * Maps FutureMaps to actual imported StreetMaps.
		 */
		Map<StreetMap, StreetMap> importedMaps = new HashMap<>();
		Set<StreetMap> failedMaps = new HashSet<>();
		for (FutureMap map : getNewOrUpdatedMaps()) {
			try (CloseableTask task = reporter
					.openTask("Importiere und speichere Karte '"
							+ map.getName() + "'")) {
				reporter.setSubTasks(new float[] { .9f, .1f });
				StreetMap importedMap;
				try (CloseableTask task2 = reporter
						.openTask("Importiere Karte '" + map.getName() + "'")) {
					importedMap = importer.importMap(map.getOsmFile(),
							map.getName(), reporter);
					if (importedMap.getEdgeBasedGraph().getNumberOfTurns() == 0) {
						// Easter Egg-ish
						JOptionPane.showMessageDialog(
								MainController.getInstance().view,
								"Your map is bad\nand you should feel bad");
						System.exit(1337);
					}
				} catch (IOException | SAXException e) {
					MainController.getInstance().view.textMessage(e
							.getMessage());
					e.printStackTrace();
					failedMaps.add(map);
					if (selectedMap != null
							&& selectedMap.getName().equals(map.getName())) {
						selectedMap = null;
					}
					continue;
				}
				try (CloseableTask task3 = reporter
						.openTask("Speichere Karte '" + importedMap.getName()
								+ "'")) {
					mapManager.saveMap(importedMap);
				} catch (IOException e) {
					MainController.getInstance().view.textMessage(e
							.getMessage());
					e.printStackTrace();
					failedMaps.add(map);
					continue;
				}
				importedMaps.put(map, importedMap);
				if (selectedMap != null
						&& selectedMap.getName().equals(map.getName())) {
					selectedMap = importedMap;
				}
			}
		}
		reporter.popTask();
		PreCalculator calculator = new PreCalculator();
		reporter.pushTask("Führe Vorberechnungen durch");
		reporter.setSubTasks(getNewPrecalculations().size());
		for (ProfileMapCombination combination : getNewPrecalculations()) {
			if (failedMaps.contains(combination.getStreetMap())) {
				continue;
			}
			reporter.pushTask("Führe Vorberechnung durch und speichere '"
					+ combination.toString() + "'");
			reporter.setSubTasks(new float[] { .95f, .05f });
			if (importedMaps.containsKey(combination.getStreetMap())) {
				combination = new ProfileMapCombination(
						importedMaps.get(combination.getStreetMap()),
						combination.getProfile());
			}
			try {
				reporter.pushTask("Führe Vorberechnung durch für '"
						+ combination + "'");
				try {
					calculator.doPrecalculation(combination, reporter);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				} finally {
					reporter.popTask("Führe Vorberechnung durch für '"
							+ combination + "'");
				}
				reporter.pushTask("Speichere '" + combination + "'");
				try {
					profileMapManager.savePrecalculation(combination);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				} finally {
					reporter.popTask("Speichere '" + combination + "'");
				}
			} finally {
				reporter.popTask("Führe Vorberechnung durch und speichere '"
						+ combination.toString() + "'");
			}
		}
		reporter.popTask();
		return selectedMap;
	}
}