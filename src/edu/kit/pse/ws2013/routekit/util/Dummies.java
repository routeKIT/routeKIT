package edu.kit.pse.ws2013.routekit.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;

import org.xml.sax.SAXException;

import edu.kit.pse.ws2013.routekit.controllers.MapManager;
import edu.kit.pse.ws2013.routekit.controllers.ProfileMapManager;
import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.models.ArcFlags;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.Weights;
import edu.kit.pse.ws2013.routekit.precalculation.DummyMapImporter;
import edu.kit.pse.ws2013.routekit.precalculation.OSMMapImporter;
import edu.kit.pse.ws2013.routekit.precalculation.PreCalculator;
import edu.kit.pse.ws2013.routekit.profiles.Profile;

public class Dummies {

	public static void createInstall(File rootDir) throws IOException,
			SAXException {
		rootDir.mkdir();
		new File(rootDir, "routeKIT.idx").createNewFile();
		ProfileMapManager.init(rootDir);

		URL url = new URL(
				"http://algo2.iti.kit.edu/documents/PSE_WS1314/karlsruhe_big.osm");
		URLConnection c = url.openConnection();
		String userpass = "pse_ws1314:pse_student_ws1314";
		String base64 = javax.xml.bind.DatatypeConverter
				.printBase64Binary(userpass.getBytes());
		String basicAuth = "Basic " + base64;
		c.setRequestProperty("Authorization", basicAuth);
		ReadableByteChannel rbc = Channels.newChannel(c.getInputStream());
		File file = File.createTempFile("routeKIT_map_", ".osm");
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();

		StreetMap karlsruhe_big = new OSMMapImporter().importMap(file,
				"Karlsruhe");
		MapManager.getInstance().saveMap(karlsruhe_big);
		ProfileMapCombination karlsruheCar = new ProfileMapCombination(
				karlsruhe_big, Profile.defaultCar);
		new PreCalculator().doPrecalculation(karlsruheCar);
		ProfileMapManager.getInstance().setCurrentCombination(karlsruheCar);
	}

	public static void createDummies(File rootDir) throws IOException,
			SAXException {
		rootDir.mkdir();
		new File(rootDir, "routeKIT.idx").createNewFile();
		ProfileMapManager.init(rootDir);
		ProfileMapCombination combination = new ProfileMapCombination(
				new DummyMapImporter()
						.importMap(new File("dummy"), "Karlsruhe"),
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
		createInstall(FileUtil.getRootDir());
	}
}
