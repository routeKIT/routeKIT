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
		runCLI("--import", "testmap1", Resources.getKarlsruheBigLocation());
		runCLI("--precalculate", "testmap1", "PKW (Standard)");
		System.gc();
		System.gc();
		System.gc();
		// if (true) {
		// return;
		// }
		runCLI("--delete-precalculation", "testmap1", "PKW (Standard)",
				"--delete-map", "testmap1");

	}

	private void runCLI(String... args) throws IOException,
			ReflectiveOperationException, InterruptedException {
		final PrintStream err = System.err;
		final PrintStream out = System.out;
		try (PrintStream newErr = new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				fail = true;
				err.write(b);
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
		if (fail) {
			Assert.fail();
		}
	}
}
