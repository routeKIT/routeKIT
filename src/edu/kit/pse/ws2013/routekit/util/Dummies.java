package edu.kit.pse.ws2013.routekit.util;

import java.io.File;
import java.io.IOException;

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
		combination.setArcFlags(new ArcFlags(new int[7]), 1000);
		combination.setWeights(new Weights(new int[7]), 1000);
		MapManager.getInstance().saveMap(combination.getStreetMap());
		ProfileMapManager.getInstance().setCurrentCombination(combination);
	}

	public static void main(String[] args) throws IOException, SAXException {
		createDummies(new File(System.getProperty("user.home"), "routeKIT"));
	}
}
