package edu.kit.pse.ws2013.routekit.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

public class TestCLI {

	private boolean fail = false;

	@Test
	public void testPrecalculate() throws IOException, InterruptedException {
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
			Thread t = new Thread(new CLI(new String[] { "--precalculate",
					"Regierungsbezirk Karlsruhe", "PKW (Standard)" }));
			t.start();
			Thread.sleep(1000);
			t.stop(); // yes, I know, leave me alone
			t.join(); // should be noop, but isn’t because precalculation is
						// multithreaded
		}
		System.setErr(err);
		System.setOut(out);
		if (fail) {
			Assert.fail();
		}
	}
}
