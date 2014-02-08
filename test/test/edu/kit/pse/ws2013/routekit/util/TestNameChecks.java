package test.edu.kit.pse.ws2013.routekit.util;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.kit.pse.ws2013.routekit.util.FileUtil;

public class TestNameChecks {

	@Test
	public void testLegacyNames() {
		for (String name : new String[] { "COM1", "com1.txt", "CON", "nul",
				"Lpt4.sys", "CLOCK$.file", "AUX", "prn" }) {
			assertFalse(FileUtil.checkMapName(name));
			assertFalse(FileUtil.checkProfileName(name));
		}
	}

	@Test
	public void testPseudoLegacyNames() {
		for (String name : new String[] { "clock", "console", "auxiliary" }) {
			assertTrue(FileUtil.checkMapName(name));
			assertTrue(FileUtil.checkProfileName(name));
		}
	}

	@Test
	public void testIllegalCharacters() {
		for (String name : new String[] { ":", "foo<bar>", "ping\7!", "what?!",
				"\0", "routeKIT has many\b\b\b\bno bugs" }) {
			assertFalse(FileUtil.checkMapName(name));
			assertFalse(FileUtil.checkProfileName(name));
		}
	}

	@Test
	public void testIllegalDirectoryNavigation() {
		for (String name : new String[] { "..", "\\", "/boot",
				"\\WINDOWS\\system32" }) {
			assertFalse(FileUtil.checkMapName(name));
			assertFalse(FileUtil.checkProfileName(name));
		}
	}

	@Test
	public void testEmpty() {
		assertFalse(FileUtil.checkMapName(""));
		assertFalse(FileUtil.checkProfileName(""));
	}

	@Test
	public void testMapNames() {
		for (String name : new String[] { "looks like map + profile",
				"\tlooks like profile" }) {
			assertFalse(FileUtil.checkMapName(name));
		}
	}

	@Test
	public void testProfileNames() {
		for (String name : new String[] { "* pseudo-selected" }) {
			assertFalse(FileUtil.checkProfileName(name));
		}
	}
}
