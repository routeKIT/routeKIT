package edu.kit.pse.ws2013.routekit.controllers;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class TestEasterEggCLI {

	private static void doTest(String[] args, String... expected) {
		StringBuilder sb = new StringBuilder();
		for (String s : expected) {
			sb.append(s);
			sb.append('\n');
		}
		final byte[] data = new byte[sb.toString().getBytes().length];
		try (PrintStream out = new PrintStream(new OutputStream() {
			int i = 0;

			@Override
			public void write(int b) throws IOException {
				data[i++] = (byte) b;
			}
		})) {
			System.setOut(out);
			new EasterEggCLI(args);
		}
		assertEquals(sb.toString(), new String(data));
	}

	@Test
	public void testMoo() {
		doTest(new String[] { "moo" }, "There is no aptitude in this program.");
	}

	@Test
	public void testVerboseMoo() {
		doTest(new String[] { "-v", "moo" },
				"Wait, that didn’t come out right. There is aptitude in this program; what I meant is that this program isn’t aptitude.");
	}

	@Test
	public void testVerboseMooWithStatement() {
		doTest(new String[] { "-v", "moo",
				"There are no easter eggs in this program." },
				"Who said that?", "              \\ /", "            -->*<--",
				"              /o\\", "             /_\\_\\",
				"            /_/_0_\\", "           /_o_\\_\\_\\",
				"          /_/_/_/_/o\\", "         /@\\_\\_\\@\\_\\_\\",
				"        /_/_/O/_/_/_/_\\", "       /_\\_\\_\\_\\_\\o\\_\\_\\",
				"      /_/0/_/_/_0_/_/@/_\\",
				"     /_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\",
				"    /_/o/_/_/@/_/_/o/_/0/_\\", "             [___]  ");
	}
}
