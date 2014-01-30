package edu.kit.pse.ws2013.routekit.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.xml.sax.SAXException;

import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.ProgressListener;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;
import edu.kit.pse.ws2013.routekit.precalculation.OSMMapImporter;
import edu.kit.pse.ws2013.routekit.precalculation.PreCalculator;
import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.util.FileUtil;

/**
 * Is the user interface and {@link ProgressListener} for commandline
 * interaction.
 * 
 */
public class CLI implements ProgressListener {

	/**
	 * Does the full import+precalculation. Progress output on commandline.
	 * 
	 * @param mapPath
	 *            path to map
	 * @param mapName
	 *            name of map
	 * @param profileName
	 *            name of profile to calculate for
	 */
	public void doImport(String mapPath, String mapName, String profileName) {
		try {
			ProgressReporter r = new ProgressReporter();
			r.addProgressListener(this);
			r.pushTask("arbeite");
			r.setSubTasks(3);
			r.pushTask("starte");
			ProfileMapManager.init(FileUtil.getRootDir(), r);
			OSMMapImporter im = new OSMMapImporter();
			r.popTask();
			r.pushTask("import");
			StreetMap sm = im.importMap(new File(mapPath), mapName, r);
			MapManager.getInstance().saveMap(sm);
			r.popTask();

			PreCalculator pc = new PreCalculator();
			Set<Profile> pf = ProfileManager.getInstance().getProfiles();
			Profile p = null;
			for (Profile profile : pf) {
				if (profile.getName().equals(profile)) {
					System.out.println(profile.getName());
					p = profile;
					break;
				}
			}
			if (p == null) {
				System.err
						.println("Profile \"" + profileName + "\" not found.");
				return;
			}
			ProfileMapCombination comb = new ProfileMapCombination(sm, p);
			r.pushTask("precalc");
			pc.doPrecalculation(comb, r);
			r.popTask();
			ProfileMapManager.getInstance().savePrecalculation(comb);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
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
