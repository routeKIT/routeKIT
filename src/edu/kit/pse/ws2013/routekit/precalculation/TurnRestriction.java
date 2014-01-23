package edu.kit.pse.ws2013.routekit.precalculation;

/**
 * A turn restriction in an OpenStreetMap file. This is only a temporary
 * representation used by the {@link OSMParser}.
 */
public class TurnRestriction {
	private final MapEdge from;
	private final MapEdge to;
	private final boolean onlyAllowedTurn;

	/**
	 * Creates a new object with the given attributes.
	 * 
	 * @param from
	 *            the edge from which a turn is restricted
	 * @param to
	 *            the edge to which a turn is restricted
	 * @param onlyAllowedTurn
	 *            whether it is the only allowed turn rather than a forbidden
	 *            turn
	 * @throws IllegalArgumentException
	 *             if {@code from} or {@code to} is {@code null}
	 */
	public TurnRestriction(MapEdge from, MapEdge to, boolean onlyAllowedTurn) {
		if (from == null || to == null) {
			throw new IllegalArgumentException();
		}

		this.from = from;
		this.to = to;
		this.onlyAllowedTurn = onlyAllowedTurn;
	}

	/**
	 * Returns the edge from which this turn restriction is defined.
	 * 
	 * @return that edge
	 */
	public MapEdge getFrom() {
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
}