package edu.kit.pse.ws2013.routekit.map;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestStreetMap {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testSaveLoad() throws IOException {
		StreetMap map = new TestDummies().getMap();
		File dir = folder.newFolder(map.getName());
		map.save(dir);
		StreetMap loaded = StreetMap.load(dir);
		StreetMap loadedLazily = StreetMap.loadLazily(dir);
		assertEquals(map, loaded);
		assertEquals(map, loadedLazily);
	}
}
