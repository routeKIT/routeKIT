package test.edu.kit.pse.ws2013.routekit.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.kit.pse.ws2013.routekit.models.ArcFlags;

public class TestArcFlags {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSaveLoad() throws IOException {
		int[] flags = new int[]{2, 4, 6892, 3, 3, 2, 4, 6, 823, 3, 4};
		ArcFlags af = new ArcFlags(flags);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(baos);
		af.save(os);
		os.flush();
		ArcFlags f = ArcFlags.load(new DataInputStream(
				new ByteArrayInputStream(baos.toByteArray())));
		for (int i = 0; i < flags.length; i++) {
			assertEquals("Diff in " + i, flags[i], f.getFlag(i));
		}
	}

}
