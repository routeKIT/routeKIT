package edu.kit.pse.ws2013.routekit.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Pattern;

import edu.kit.pse.ws2013.routekit.history.History;

public class FileUtil {
	/**
	 * Recursively deletes a directory.
	 * 
	 * @param directory
	 *            The directory.
	 * @throws IOException
	 *             If something goes wrong.
	 */
	public static void rmRf(File directory) throws IOException {
		// recursive directory delete:
		// http://stackoverflow.com/a/8685959/1420237
		Files.walkFileTree(directory.toPath(), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc)
					throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
				if (exc == null) {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				} else {
					throw exc;
				}
			}
		});
	}

	/**
	 * Gets the root directory of routeKIT’s saved data (profiles, maps,
	 * precalculations).
	 * <ul>
	 * <li><b>Windows:</b> {@code %APPDATA%/routeKIT}</li>
	 * <li><b>Mac:</b> {@code $HOME/Library/Application Support/routeKIT}</li>
	 * <li><b>Unix/Linux:</b> {@code $HOME/.config/routeKIT}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             If the operating system is neither of the above.
	 */
	public static File getRootDir() throws IOException {
		String os = System.getProperty("os.name").toUpperCase();
		if (os.contains("WIN")) {
			return new File(System.getenv("APPDATA"), "routeKIT");
		} else if (os.contains("MAC")) {
			return new File(new File(new File(System.getProperty("user.home"),
					"Library"), "Application Support"), "routeKIT");
		} else if (os.matches(".*N[IU]X.*")) {
			return new File(
					new File(System.getProperty("user.home"), ".config"),
					"routeKIT");
		} else {
			throw new IOException("Unknown operating system " + os);
		}
	}

	/**
	 * Gets the file in which routeKIT’s {@link History} should be saved.
	 */
	public static File getHistoryFile() throws IOException {
		return new File(getRootDir(), "routeKIT.history");
	}

	// @formatter:off – uses bad indentation
	private static Pattern illegalNames = Pattern.compile(
			// relics from the past
			"^CON(\\..*)?$|^PRN(\\..*)?$|^AUX(\\..*)?$|^CLOCK\\$(\\..*)?$|^NUL(\\..*)?$|^COM\\d(\\..*)?$|^LPT\\d(\\..*)?$|"
			// other Windows forbidden characters: <>:"/\|?* and 0-31
			+ "<|>|:|\"|/|\\\\|\\||\\?|\\*|[\0-\31]|"
			// forbidden everywhere: ., .., <empty>
			+ "^\\.?\\.?$", Pattern.CASE_INSENSITIVE);
	// @formatter:on

	/**
	 * Returns true iff the given name is a legal map name.
	 * <p>
	 * Note that this only checks the general validity of the name; you should
	 * also verify that there is no other map whose name differs only in casing
	 * ("MyMap" vs "mymap").
	 * 
	 * @param name
	 *            The map name.
	 * @return {@code true} if {@code name} can be used as a map name,
	 *         {@code false} otherwise.
	 */
	public static boolean checkMapName(String name) {
		return !name.startsWith("\t") && !name.contains(" + ")
				&& !illegalNames.matcher(name).find();
	}

	/**
	 * Returns true iff the given name is a legal profile name.
	 * <p>
	 * Note that this only checks the general validity of the name; you should
	 * also verify that there is no other profile whose name differs only in
	 * casing ("MyProfile" vs "myprofile").
	 * 
	 * @param name
	 *            The profile name.
	 * @return {@code true} if {@code name} can be used as a profile name,
	 *         {@code false} otherwise.
	 */
	public static boolean checkProfileName(String name) {
		return !name.startsWith(" *") && !illegalNames.matcher(name).find();
	}
}
