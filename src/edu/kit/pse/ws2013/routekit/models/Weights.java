package edu.kit.pse.ws2013.routekit.models;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Arrays;

/**
 * Stores edge weights for a precalculated graph.
 */
public class Weights {
	/**
	 * The weights of the turns.
	 */
	private int[] weights;

	/**
	 * Creates a new {@link Weights} object with the given weights.
	 * 
	 * @param weights
	 *            The weights.
	 */
	public Weights(int[] weights) {
		this.weights = weights;
	}

	/**
	 * Returns the weight of the given turn.
	 * 
	 * @param turn
	 *            The ID of the turn.
	 * @return The weight of the turn.
	 */
	public int getWeight(int turn) {
		return weights[turn];
	}

	public void save(File f) throws IOException {
		try (RandomAccessFile raf = new RandomAccessFile(f, "rw");
				FileChannel fc = raf.getChannel()) {
			MappedByteBuffer mbb = fc.map(MapMode.READ_WRITE, 0,
					(1 + weights.length) * 4);
			mbb.order(ByteOrder.BIG_ENDIAN).asIntBuffer().put(weights.length)
					.put(weights);
			mbb.force();
		}
	}

	public static Weights load(File f) throws IOException {
		try (FileInputStream fis = new FileInputStream(f);
				DataInputStream dis = new DataInputStream(fis);
				FileChannel fc = fis.getChannel()) {
			int length = dis.readInt();
			MappedByteBuffer mbb = fc.map(MapMode.READ_ONLY, 4, length * 4);
			int[] weights = new int[length];
			mbb.order(ByteOrder.BIG_ENDIAN).asIntBuffer().get(weights);
			return new Weights(weights);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Weights) {
			return Arrays.equals(weights, ((Weights) obj).weights);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return weights[0];
	}
}
