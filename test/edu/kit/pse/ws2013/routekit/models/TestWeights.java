package edu.kit.pse.ws2013.routekit.models;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.junit.Test;

public class TestWeights {

	@Test
	public void testSaveLoad() throws IOException {
		final int length = 1000000;
		int[] weights = new int[length];
		Random r = new Random();
		for (int i = 0; i < length; i++) {
			weights[i] = r.nextInt();
		}
		Weights w = new Weights(weights);
		File f = File.createTempFile("routeKIT_testWeights_", ".weights");
		w.save(f);
		Weights w2 = Weights.load(f);
		for (int i = 0; i < weights.length; i++) {
			assertEquals(w.getWeight(i), w2.getWeight(i));
		}
	}
}
