package edu.kit.pse.ws2013.routekit.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
		ProfileMapManager.init(rootDir, new DummyProgressReporter());

		URL url = new URL(
				"http://download.geofabrik.de/europe/germany/baden-wuerttemberg/karlsruhe-regbez-latest.osm.bz2");
		ReadableByteChannel rbc = Channels.newChannel(url.openConnection()
				.getInputStream());
		File file = File.createTempFile("routeKIT_map_", ".osm");
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();

		StreetMap karlsruhe_big = new OSMMapImporter().importMap(file,
				"Karlsruhe", new DummyProgressReporter());
		MapManager.getInstance().saveMap(karlsruhe_big);
		Properties p = new Properties();
		p.setProperty("default", "true");
		try (FileWriter writer = new FileWriter(new File(new File(rootDir,
				"Karlsruhe"), "Karlsruhe.properties"))) {
			p.store(writer, null);
		}
		ProfileMapCombination karlsruheCar = new ProfileMapCombination(
				karlsruhe_big, Profile.defaultCar);
		new PreCalculator().doPrecalculation(karlsruheCar,
				new DummyProgressReporter());
		ProfileMapManager.getInstance().setCurrentCombination(karlsruheCar);
	}

	public static void createDummies(File rootDir) throws IOException,
			SAXException {
		rootDir.mkdir();
		new File(rootDir, "routeKIT.idx").createNewFile();
		ProfileMapManager.init(rootDir, new DummyProgressReporter());
		ProfileMapCombination combination = new ProfileMapCombination(
				new DummyMapImporter().importMap(new File("dummy"),
						"Karlsruhe", new DummyProgressReporter()),
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

	private static void extractTo(File target) {
		try {
			URL u = new URL("http://felix.dogcraft.de/routeKIT.zip");
			ZipInputStream zis = new ZipInputStream(u.openStream());
			ZipEntry ze;
			byte[] buf = new byte[4096];
			while ((ze = zis.getNextEntry()) != null) {
				int len = 0;
				File f = new File(target, ze.getName());
				if (ze.isDirectory()) {
					f.mkdirs();
					continue;
				}
				FileOutputStream fos = new FileOutputStream(f);
				while ((len = zis.read(buf)) > 0) {
					fos.write(buf, 0, len);
				}
				fos.close();
				System.out.println(ze.getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException, SAXException {
		// createInstall(FileUtil.getRootDir());
		Dummies.extractTo(new File("out"));
	}
}
