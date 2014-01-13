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

/**
 * Enthält die Kantengewichte für den vorberechneten Graphen.
 */
public class Weights {
	/**
	 * Die Gewichte der Abbiegevorgänge.
	 */
	private int[] weights;

	/**
	 * Erstellt ein neues Weights-Objekt mit den übergebenen Kantengewichten.
	 * 
	 * @param weights
	 *            Die Kantengewichte.
	 */
	public Weights(int[] weights) {
		this.weights = weights;
	}

	/**
	 * Gibt das zum Abbiegevorgang gehörende Gewicht zurück.
	 * 
	 * @param turn
	 *            Die Nummer eines Abbiegevorgang.
	 * @return Das Kantengwicht des Abbiegevorgangs.
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
}
