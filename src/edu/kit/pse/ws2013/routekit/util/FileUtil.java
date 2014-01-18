package edu.kit.pse.ws2013.routekit.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

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
}
