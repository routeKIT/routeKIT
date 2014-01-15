package edu.kit.pse.ws2013.routekit.map;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.models.Weights;
import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * Enthält das Straßennetz als kantenbasierten Graphen. Die Knoten dieses
 * Graphen entsprechen den Kanten des zugehörigen {@link Graph}-Objekts und
 * werden daher mit {@code Edge} bezeichnet. Die Kanten dieses Graphen
 * repräsentieren Abbiegemöglichkeiten und werden mit {@code Turn} bezeichnet.
 * 
 * Diese Datenstruktur ist unabhängig vom Profil und wird wie {@link Graph} bei
 * der Vorberechnung für eine Karte erstellt. Erst in Kombination mit den
 * profilspezifischen {@link Weights} kann sie zur Routenberechnung verwendet
 * werden.
 */
public class EdgeBasedGraph {
	private int[] partitions;
	private int[] edges;
	private int[] turns;
	private TurnType[] turnTypes;
	private Map<Integer, Restriction> restrictions;
	private int[] turnsReverse;

	/**
	 * Konstruktor: Erzeugt ein neues Objekt aus dem gegebenen Adjazenzfeld.
	 * 
	 * @param edges
	 *            Das Knoten-Array (Kanten im Straßengraph) des Adjazenzfelds.
	 * @param turns
	 *            Das Kanten-Array (Abbiegemöglichkeiten) des Adjazenzfelds.
	 * @param turnTypes
	 *            Die Typen der Abbiegemöglichkeiten.
	 * @param restrictions
	 *            Die Beschränkungen der Abbiegemöglichkeiten.
	 */
	public EdgeBasedGraph(int[] edges, int[] turns, TurnType[] turnTypes,
			Map<Integer, Restriction> restrictions) {
		this.edges = edges;
		this.turns = turns;
		this.turnTypes = turnTypes;
		this.restrictions = restrictions;
		this.turnsReverse = new int[edges.length];
		int currentEdge = 0;
		for (int i = 0; i < turns.length; i++) {
			while (currentEdge + 1 < edges.length
					&& edges[currentEdge + 1] <= i) {
				currentEdge++;

			}
			turnsReverse[i] = currentEdge;
		}
	}

	public int[] getEdges() {
		return edges;
	}

	/**
	 * Gibt die Partition zurück, in der sich die angegebene Kante (der Knoten
	 * des kantenbasierten Graphen) befindet.
	 * 
	 * Ist noch keine Partitionierung gegeben, so wird immer eine
	 * Standard-Partition zurückgegeben.
	 * 
	 * @param edge
	 *            Die Kante, deren Partition bestimmt werden soll.
	 * @return
	 */
	public int getPartition(int edge) {
		return partitions[edge];
	}

	/**
	 * Setzt die Partitionen des Graphen. Die {@code Edge}s des Graphen sind die
	 * Indizes in {@code partitions}.
	 * 
	 * @param partitions
	 *            Die neuen Partitionen.
	 */
	public void setPartitions(int[] partitions) {
		this.partitions = partitions;
	}

	/**
	 * Gibt die Kante zurück, auf die die angegebene Abbiegemöglichkeit führt.
	 * 
	 * @param turn
	 *            Die Abbiegemöglichkeit, deren Endkante gesucht wird.
	 * @return
	 */
	public int getTargetEdge(int turn) {
		return turns[turn];
	}

	/**
	 * Gibt die Art des angegebenen Abbiegevorgangs zurück.
	 * 
	 * @param turn
	 *            Der Abbiegevorgang, dessen Art gesucht wird.
	 * @return
	 */
	public TurnType getTurnType(int turn) {
		return turnTypes[turn];
	}

	/**
	 * Gibt die Kante zurück, von der die angegebene Abbiegemöglichkeit besteht.
	 * 
	 * @param turn
	 *            Die Abbiegemöglichkeit, deren Anfangskante gesucht wird.
	 * @return
	 */
	public int getStartEdge(int turn) {
		return turnsReverse[turn];
	}

	/**
	 * Bestimmt, ob der angegebene Abbiegevorgang unter dem angegeben Profil
	 * zulässig ist.
	 * 
	 * @param turn
	 *            Der zu betrachtende Abbiegevorgang.
	 * @param profile
	 *            Das verwendete Profil.
	 * @return
	 */
	public boolean allowsTurn(int turn, Profile profile) {
		return restrictions.get(turn).allows(profile);
	}

	/**
	 * Gibt alle Abbiegemöglichkeiten <b>von</b> der angegebenen Kante zurück.
	 * 
	 * @param edge
	 *            Die Kante, deren ausgehende Abbiegemöglichkeiten gesucht
	 *            werden.
	 * @return
	 */
	public Set<Integer> getOutgoingTurns(int edge) {
		return new IntArraySet(edges[edge],
				(edge == edges.length - 1 ? turns.length : edges[edge + 1])
						- edges[edge], turns);
	}

	/**
	 * Gibt alle Abbiegemöglichkeiten <b>auf</b> die angegebene Kante zurück.
	 * 
	 * @param edge
	 *            Die Kante, deren eingehende Abbiegemöglichkeiten gesucht
	 *            werden.
	 * @return
	 */
	public Set<Integer> getIncomingTurns(int edge) {
		throw new Error("Unimplemented");
	}

	public int getNumberOfTurns() {
		return turns.length;
	}

	/**
	 * Save this edge-based graph to the specified file.
	 * <p>
	 * The only thing you need to know about the format of the file is that
	 * {@link #load(File)} can parse it; the rest of this documentation is
	 * purely informational, as the format may change in the future (but we’ll
	 * try and maintain backwards compatibility).
	 * <h4>The file format</h4>
	 * <h5>Header</h5>
	 * <ul>
	 * <li>Magic: bytes {@code 0x00}, {@code 0x02}, ASCII {@code routeKIT}</li>
	 * <li>ASCII {@code e} (for "edge-based graph")</li>
	 * <li>byte {@code 0x01} (for version 1, but future versions won’t
	 * necessarily use the same header format)</li>
	 * <li>ASCII {@code End of Transmission}</li>
	 * </ul>
	 * <h5>Edges, Turns, Partitions</h5>
	 * <ul>
	 * <li>int number of edges, int number of turns (both four bytes, high byte
	 * first)</li>
	 * <li>ints edges (all four bytes, high byte first)</li>
	 * <li>ints turns (all four bytes, high byte first)</li>
	 * <li>ints partitions (all four bytes, high byte first)</li>
	 * <li>bytes turn types (all one byte)</li>
	 * </ul>
	 * <h5>Restrictions</h5>
	 * <ul>
	 * <li>For each {@link Restriction}, int number of turns with this
	 * restriction (four bytes, high byte first), then the {@link Restriction}
	 * (see {@link Restriction#save(java.io.DataOutput)}), then ints turns (all
	 * four bytes, high byte first)</li>
	 * <li>int {@code 0} (four bytes, high byte first)</li>
	 * </ul>
	 * 
	 * @param file
	 *            The file.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void save(File file) throws IOException {
		try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {

			raf.writeUTF("routeKIT");
			raf.writeByte(0x65); // 'e', for "edge-based graph"
			raf.writeByte(1); // version 1
			raf.writeByte(0x04); // End of Transmission

			int nEdges = edges.length;
			int nTurns = turns.length;
			raf.writeInt(nEdges);
			raf.writeInt(nTurns);
			final int headerLength = 2 + "routeKIT".length() + 3 + 8;
			assert (raf.getFilePointer() == headerLength);
			int intsLength = nEdges * 4 + nTurns * 4 + nEdges * 4;
			int bytesLength = nTurns;
			final int dataLength = intsLength + bytesLength;
			MappedByteBuffer b = raf.getChannel().map(MapMode.READ_WRITE,
					headerLength, dataLength);
			IntBuffer ints = b.asIntBuffer();
			ints.put(edges, 0, nEdges);
			ints.put(turns, 0, nTurns);
			ints.put(partitions, 0, nEdges);
			assert (ints.position() == intsLength / 4);
			b.position(intsLength);
			byte[] byteTurnTypes = new byte[nTurns];
			for (int i = 0; i < nTurns; i++) {
				byteTurnTypes[i] = (byte) turnTypes[i].ordinal();
			}
			b.put(byteTurnTypes, 0, nTurns);
			assert (b.position() == dataLength);

			raf.seek(headerLength + dataLength);

			Map<Restriction, Set<Integer>> reverseRestrictions = new HashMap<>();
			for (int i = 0; i < nTurns; i++) {
				Restriction r = restrictions.get(i);
				if (r != null) {
					Set<Integer> current = reverseRestrictions.get(r);
					if (current == null) {
						current = new HashSet<>();
					}
					current.add(i);
					reverseRestrictions.put(r, current);
				}
			}
			for (Entry<Restriction, Set<Integer>> entry : reverseRestrictions
					.entrySet()) {
				raf.writeInt(entry.getValue().size());
				entry.getKey().save(raf);
				for (int i : entry.getValue()) {
					raf.writeInt(i);
				}
			}
			raf.writeInt(0);
		}
	}

	/**
	 * Loads a graph from the specified file.
	 * <p>
	 * This method can parse files generated by the current version of
	 * {@link #save(File)}, and it will also attempt to parse files generated by
	 * earlier versions of that method. For the file format, see there.
	 * 
	 * @param file
	 *            The file.
	 * @return A {@link Graph} loaded from {@code file}.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public static EdgeBasedGraph load(File file) throws IOException {
		try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
			if (!raf.readUTF().equals("routeKIT")) {
				throw new IOException("Wrong magic!");
			}
			if (raf.readByte() != 0x65) { // 'e', for "edge-based graph"
				throw new IOException("Wrong file type!");
			}
			if (raf.readByte() != 1) { // version 1
				throw new IOException("Unsupported version!");
			}
			if (raf.readByte() != 0x04) { // End of Transmission
				throw new IOException("Wrong magic!");
			}

			int nEdges = raf.readInt();
			int nTurns = raf.readInt();
			final int headerLength = 2 + "routeKIT".length() + 3 + 8;
			assert (raf.getFilePointer() == headerLength);
			int intsLength = nEdges * 4 + nTurns * 4 + nEdges * 4;
			int bytesLength = nTurns;
			final int dataLength = intsLength + bytesLength;
			int[] edges = new int[nEdges];
			int[] turns = new int[nTurns];
			int[] partitions = new int[nEdges];
			byte[] byteTurnTypes = new byte[nTurns];
			MappedByteBuffer b = raf.getChannel().map(MapMode.READ_ONLY,
					headerLength, dataLength);
			IntBuffer ints = b.asIntBuffer();
			ints.get(edges, 0, nEdges);
			ints.get(turns, 0, nTurns);
			ints.get(partitions, 0, nEdges);
			b.position(intsLength);
			b.get(byteTurnTypes, 0, nTurns);
			TurnType[] turnTypes = new TurnType[nTurns];
			for (int i = 0; i < nTurns; i++) {
				turnTypes[i] = TurnType.values()[byteTurnTypes[i]];
			}
			assert (b.position() == dataLength);

			raf.seek(headerLength + dataLength);

			Map<Integer, Restriction> restrictions = new HashMap<>();
			int length;
			while ((length = raf.readInt()) != 0) {
				Restriction r = Restriction.load(raf);
				while (length-- > 0) {
					restrictions.put(raf.readInt(), r);
				}
			}

			EdgeBasedGraph ret = new EdgeBasedGraph(edges, turns, turnTypes,
					restrictions);
			ret.setPartitions(partitions);
			return ret;
		}
	}
}
