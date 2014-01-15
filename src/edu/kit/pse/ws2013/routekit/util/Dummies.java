package edu.kit.pse.ws2013.routekit.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.xml.sax.SAXException;

import edu.kit.pse.ws2013.routekit.controllers.MapManager;
import edu.kit.pse.ws2013.routekit.controllers.ProfileMapManager;
import edu.kit.pse.ws2013.routekit.models.ArcFlags;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.Weights;
import edu.kit.pse.ws2013.routekit.precalculation.MapImporter;
import edu.kit.pse.ws2013.routekit.profiles.Profile;

public class Dummies {
	public static void createDummies(File rootDir) throws IOException,
			SAXException {
		rootDir.mkdir();
		new File(rootDir, "routeKIT.idx").createNewFile();
		ProfileMapManager.init(rootDir);
		ProfileMapCombination combination = new ProfileMapCombination(
				new MapImporter().importMap(new File("dummy"), "Karlsruhe"),
				Profile.defaultCar);
		int[] arc = new int[7];
		int[] w = new int[7];
		Arrays.fill(arc, -1);
		Arrays.fill(w, 1);
		combination.setArcFlags(new ArcFlags(arc), 1000);
		combination.setWeights(new Weights(w), 1000);
		MapManager.getInstance().saveMap(combination.getStreetMap());
		ProfileMapManager.getInstance().setCurrentCombination(combination);
	}

	public static void main(String[] args) throws IOException, SAXException {
		createDummies(new File(System.getProperty("user.home"), "routeKIT"));
	}
}
