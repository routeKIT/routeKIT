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
			sb.append(System.getProperty("line.separator"));
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

	@Test
	public void testHelp() {
		doTest(new String[] { "--help" },
				"routeKIT: Programm zur Routenplanung und -berechnung.", "",
				"Optionen:", "", "  --help", "  --usage",
				"      Gibt diesen Hilfetext aus.", "  --version",
				"      Gibt die Version von routeKIT aus.",
				"  --import <Name> <Datei>", "  --import-map <Name> <Datei>",
				"      Importiert eine Karte aus einer OSM-Datei.",
				"  --update <Name> <Datei>", "  --update-map <Name> <Datei>",
				"      Aktualisiert eine Karte aus einer OSM-Datei.",
				"  --delete-map <Name>", "      Löscht eine Karte.",
				"  --select <Kartenname> <Profilname>",
				"      Wählt eine Kombination aus.",
				"  --delete-precalculation <Kartenname> <Profilname>",
				"      Löscht eine Vorberechnung.",
				"  --precalculate <Kartenname> <Profilname>",
				"      Führt eine Vorberechnung durch.", "",
				"Dieses Programm ist nicht aptitude.");
	}

	@Test
	public void testVersion() {
		doTest(new String[] { "--version" }, "routeKIT version 1.0.0");
	}
}
