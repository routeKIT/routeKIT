package edu.kit.pse.ws2013.routekit.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.itadaki.bzip2.BZip2InputStream;
import org.xml.sax.SAXException;

import edu.kit.pse.ws2013.routekit.controllers.MapManager;
import edu.kit.pse.ws2013.routekit.controllers.ProfileMapManager;
import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;
import edu.kit.pse.ws2013.routekit.precalculation.OSMMapImporter;
import edu.kit.pse.ws2013.routekit.precalculation.PreCalculator;
import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * Contains three methods which create a more or less useful routeKIT
 * installation.
 */
public class Dummies {
	/**
	 * Creates a full-fledged installation by downloading a map of the
	 * governmental district of Karlsruhe from {@code geofabrik.de} and
	 * executing a precalculation for {@link Profile#defaultCar}.
	 * <p>
	 * {@link MapManager} and {@link ProfileMapManager} are expected to have
	 * been initialized already.
	 * 
	 * @param rootDir
	 *            The root directory of the routeKIT installation.
	 * @param pr
	 *            The {@link ProgressReporter} to report progress to.
	 */
	public static void createInstall(File rootDir, ProgressReporter pr)
			throws IOException, SAXException {
		pr.setSubTasks(new float[] { .05f, .05f, .9f });
		pr.pushTask("Lade Standardkarte herunter");
		URL url = new URL(
				"http://download.geofabrik.de/europe/germany/baden-wuerttemberg/karlsruhe-regbez-latest.osm.bz2");
		File file = File.createTempFile("routeKIT_map_", ".osm");
		try (InputStream is = new BZip2InputStream(url.openStream(), false);
				FileOutputStream fos = new FileOutputStream(file)) {
			byte[] data = new byte[65536];
			while (true) {
				int read = is.read(data);
				if (read == -1) {
					break;
				}
				fos.write(data, 0, read);
			}

			pr.nextTask("Importiere und speichere Standardkarte");
			StreetMap karlsruhe_big = new OSMMapImporter().importMap(file,
					"Karlsruhe", pr);
			MapManager.getInstance().saveMap(karlsruhe_big);
			Properties p = new Properties();
			p.setProperty("default", "true");
			try (FileWriter writer = new FileWriter(new File(new File(rootDir,
					"Karlsruhe"), "Karlsruhe.properties"))) {
				p.store(writer, null);
			}
			pr.nextTask("Erstelle Standard-Vorberechnung");
			ProfileMapCombination karlsruheCar = new ProfileMapCombination(
					karlsruhe_big, Profile.defaultCar);
			new PreCalculator().doPrecalculation(karlsruheCar, pr);
			ProfileMapManager.getInstance().setCurrentCombination(karlsruheCar);
			ProfileMapManager.getInstance().savePrecalculation(karlsruheCar);
			pr.popTask();
		}
	}

	/**
	 * Downloads a complete routeKIT installation from {@code dogcraft.de} and
	 * extracts it to {@code rootDir}.
	 * 
	 * @param rootDir
	 *            The root directory of the routeKIT installation.
	 */
	public static void downloadInstall(File rootDir) {
		try {
			URL u = new URL("http://felix.dogcraft.de/routeKIT.zip");
			try (ZipInputStream zis = new ZipInputStream(u.openStream())) {
				ZipEntry ze;
				byte[] buf = new byte[4096];
				while ((ze = zis.getNextEntry()) != null) {
					int len = 0;
					String name = ze.getName();
					String sub = name.substring(name.indexOf("/") + 1);
					File f = new File(rootDir, sub);
					if (ze.isDirectory()) {
						f.mkdirs();
						continue;
					}
					try (FileOutputStream fos = new FileOutputStream(f)) {
						while ((len = zis.read(buf)) > 0) {
							fos.write(buf, 0, len);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e1) {
			// this doesn’t happen
		}
	}

	/**
	 * Runs {@link #downloadInstall(File)}.
	 * 
	 * @param args
	 *            Ignored.
	 */
	public static void main(String[] args) throws IOException, SAXException {
		// createInstall(FileUtil.getRootDir());
		Dummies.downloadInstall(new File("out"));
	}
}
