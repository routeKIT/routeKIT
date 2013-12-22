package map;
import java.util.Map;
import java.util.Set;

import models.Weights;
import profiles.Profile;
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
		return 0;
	}
	/**
	 * Setzt die Partitionen des Graphen. Die {@code Edge}s des Graphen sind die
	 * Indizes in {@code partitions}.
	 * 
	 * @param partitions
	 *            Die neuen Partitionen.
	 */
	public void setPartitions(int[] partitions) {
	}
	/**
	 * Gibt die Kante zurück, auf die die angegebene Abbiegemöglichkeit führt.
	 * 
	 * @param turn
	 *            Die Abbiegemöglichkeit, deren Endkante gesucht wird.
	 * @return
	 */
	public int getTargetEdge(int turn) {
		return 0;
	}
	/**
	 * Gibt die Art des angegebenen Abbiegevorgangs zurück.
	 * 
	 * @param turn
	 *            Der Abbiegevorgang, dessen Art gesucht wird.
	 * @return
	 */
	public TurnType getTurnType(int turn) {
		return null;
	}
	/**
	 * Gibt die Kante zurück, von der die angegebene Abbiegemöglichkeit besteht.
	 * 
	 * @param turn
	 *            Die Abbiegemöglichkeit, deren Anfangskante gesucht wird.
	 * @return
	 */
	public int getStartEdge(int turn) {
		return 0;
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
		return false;
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
		return null;
	}
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
		return null;
	}
}
