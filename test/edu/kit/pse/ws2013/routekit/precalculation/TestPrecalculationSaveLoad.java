package edu.kit.pse.ws2013.routekit.precalculation;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.kit.pse.ws2013.routekit.controllers.ProfileMapManager;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.util.DummyProgressReporter;
import edu.kit.pse.ws2013.routekit.util.FileUtil;

public class TestPrecalculationSaveLoad {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testSaveLoad() throws IOException {
		ProfileMapManager manager;
		try {
			manager = ProfileMapManager.getInstance();
		} catch (IllegalStateException e) {
			ProfileMapManager.init(FileUtil.getRootDir(),
					new DummyProgressReporter());
			manager = ProfileMapManager.getInstance();
		}
		ProfileMapCombination precalc = manager.getPrecalculations().iterator()
				.next();
		File dir = folder.newFolder();
		precalc.save(dir);
		ProfileMapCombination loaded = ProfileMapCombination.load(
				precalc.getProfile(), precalc.getStreetMap(), dir);
		ProfileMapCombination loadedLazily = ProfileMapCombination.loadLazily(
				precalc.getProfile(), precalc.getStreetMap(), dir);
		assertEquals(precalc, loaded);
		assertEquals(precalc, loadedLazily);
	}
}
