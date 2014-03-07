package edu.kit.pse.ws2013.routekit.controllers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Assert;
import org.junit.Test;

import edu.kit.pse.ws2013.routekit.Resources;

public class TestCLI {

	private boolean fail = false;

	private Runnable testCLI(String[] args) throws IOException,
			ReflectiveOperationException {
		ClassLoader c = new URLClassLoader(new URL[] { new File("bin").toURI()
				.toURL() }, TestCLI.class.getClassLoader().getParent());
		Class<Runnable> cli = (Class<Runnable>) c
				.loadClass("edu.kit.pse.ws2013.routekit.controllers.CLI");
		return cli.getConstructor(String[].class).newInstance((Object) args);
	}

	@Test
	public void testPrecalculate() throws IOException, InterruptedException,
			ReflectiveOperationException {

		runCLI(false, "--import", "testmap1",
				Resources.getKarlsruheBigLocation());
		runCLI(false, "--select", "testmap1", "PKW (Standard)");
		runCLI(true, "--import", "testmap1",
				Resources.getKarlsruheBigLocation());
		runCLI(true, "--update", "testmap2",
				Resources.getKarlsruheBigLocation());
		runCLI(true, "--import");
		runCLI(true, "--import", " + ", Resources.getKarlsruheBigLocation());
		runCLI(true, "--delete-map");
		runCLI(false, "--precalculate", "testmap1", "PKW (Standard)");
		runCLI(true, "--precalculate", "testmap1", "PKW Non-existent");

		runCLI(false, "--select", "Regierungsbezirk Karlsruhe",
				"PKW (Standard)");
		System.gc();
		System.gc();
		System.gc();
		// if (true) {
		// return;
		// }
		runCLI(true, "--delete-precalculation", "testmap1", "LKW (Standard)");
		runCLI(false, "--delete-precalculation", "testmap1", "PKW (Standard)",
				"--delete-map", "testmap1");

	}

	private void runCLI(final boolean shouldFail, String... args)
			throws IOException, ReflectiveOperationException,
			InterruptedException {
		final PrintStream err = System.err;
		final PrintStream out = System.out;
		fail = false;
		try (PrintStream newErr = new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				fail = true;
				if (!shouldFail) {
					err.write(b);
				}
			}
		}); PrintStream newOut = new PrintStream(new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				// ignore
			}
		})) {
			System.setErr(newErr);
			System.setOut(newOut);
			testCLI(args).run();
		}
		System.setErr(err);
		System.setOut(out);
		if (shouldFail ^ fail) {
			Assert.fail();
		}
	}
}
