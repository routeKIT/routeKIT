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
 * Enthält die Arc-Flags für den vorberechneten Graphen.
 */
public class ArcFlags {

	/**
	 * Die Arc-Flags des Graphen (als long-Bitvektoren).
	 */
	private int[] flags;

	/**
	 * Erstellt ein neues ArcFlags-Objekt mit den übergebenen Arc-Flags.
	 * 
	 * @param flags
	 *            Die Arc-Flags.
	 */
	public ArcFlags(int[] flags) {
		this.flags = flags;
	}

	/**
	 * Gibt die Arc-Flags des angegebenen Abbiegevorgangs zurück.
	 * 
	 * @param turn
	 *            Die Nummer des Abbiegevorgangs.
	 * @return Die Flags
	 */
	public int getFlag(int turn) {
		return flags[turn];
	}

	public void save(File f) throws IOException {
		try (RandomAccessFile raf = new RandomAccessFile(f, "rw");
				FileChannel fc = raf.getChannel()) {
			MappedByteBuffer mbb = fc.map(MapMode.READ_WRITE, 0,
					(1 + flags.length) * 4);
			mbb.order(ByteOrder.BIG_ENDIAN).asIntBuffer().put(flags.length)
					.put(flags);
			mbb.force();
		}
	}

	public static ArcFlags load(File f) throws IOException {
		try (FileInputStream fis = new FileInputStream(f);
				DataInputStream dis = new DataInputStream(fis);
				FileChannel fc = fis.getChannel()) {
			int length = dis.readInt();
			MappedByteBuffer mbb = fc.map(MapMode.READ_ONLY, 4, length * 4);
			int[] flags = new int[length];
			mbb.order(ByteOrder.BIG_ENDIAN).asIntBuffer().get(flags);
			return new ArcFlags(flags);
		}
	}
}
