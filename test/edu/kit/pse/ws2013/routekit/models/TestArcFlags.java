package edu.kit.pse.ws2013.routekit.models;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class TestArcFlags {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSaveLoad() throws IOException {
		final int length = 1000000;
		int[] flags = new int[length];
		Random r = new Random();
		for (int i = 0; i < length; i++) {
			flags[i] = r.nextInt();
		}
		ArcFlags af = new ArcFlags(flags);
		File f = File.createTempFile("routeKIT_testWeights_", ".weights");
		af.save(f);
		ArcFlags af2 = ArcFlags.load(f);
		for (int i = 0; i < flags.length; i++) {
			assertEquals(af.getFlag(i), af2.getFlag(i));
		}
	}

}
