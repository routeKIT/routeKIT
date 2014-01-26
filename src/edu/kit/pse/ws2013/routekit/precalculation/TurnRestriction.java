package edu.kit.pse.ws2013.routekit.precalculation;

/**
 * A turn restriction in an OpenStreetMap file. This is only a temporary
 * representation used by the {@link OSMParser}.
 */
public class TurnRestriction {
	private final int from;
	private final MapEdge to;
	private final boolean onlyAllowedTurn;

	/**
	 * Creates a new object with the given attributes.
	 * 
	 * @param from
	 *            the ID of the OSM way from which a turn is restricted
	 * @param to
	 *            the edge to which a turn is restricted
	 * @param onlyAllowedTurn
	 *            whether it is the only allowed turn rather than a forbidden
	 *            turn
	 * @throws IllegalArgumentException
	 *             if {@code to} is {@code null}
	 */
	public TurnRestriction(int from, MapEdge to, boolean onlyAllowedTurn) {
		if (to == null) {
			throw new IllegalArgumentException();
		}

		this.from = from;
		this.to = to;
		this.onlyAllowedTurn = onlyAllowedTurn;
	}

	/**
	 * Returns the identifier of the OSM way from which this turn restriction is
	 * defined.
	 * 
	 * @return that OSM way identifier
	 */
	public int getFrom() {
		return from;
	}

	/**
	 * Returns the edge to which this turn restriction is defined.
	 * 
	 * @return that edge
	 */
	public MapEdge getTo() {
		return to;
	}

	/**
	 * Indicates whether this is the only allowed turn from the edge rather than
	 * a forbidden turn.
	 * 
	 * @return {@code true} if only this turn is allowed, or {@code false} if
	 *         all other turns are allowed except this one
	 */
	public boolean isOnlyAllowedTurn() {
		return onlyAllowedTurn;
	}

	/**
	 * Determines whether this restriction allows turning into the given edge.
	 * 
	 * @param to
	 *            the edge
	 * @return {@code true} if turning into {@code edge} is allowed, otherwise
	 *         {@code false}
	 */
	public boolean allowsTo(MapEdge to) {
		return (onlyAllowedTurn && this.to == to)
				|| (!onlyAllowedTurn && this.to != to);
	}
}