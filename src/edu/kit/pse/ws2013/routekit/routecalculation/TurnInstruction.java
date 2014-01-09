package edu.kit.pse.ws2013.routekit.routecalculation;

/**
 * A single turn instruction which is part of a {@link RouteDescription}.
 */
public class TurnInstruction {
	private int turn;
	private String text;
	
	/**
	 * Creates a new {@code TurnInstruction} with the given attributes. 
	 * 
	 * @param turn
	 *            the turn this instruction describes
	 * @param text
	 *            the instruction text
	 */
	public TurnInstruction(int turn, String text) {
		this.turn = turn;
		this.text = text;
	}

	/**
	 * Returns the turn which this instruction describes.
	 * @return the described turn
	 */
	public int getTurn() {
		return turn;
	}

	/**
	 * Returns a string representation of this turn instruction.
	 * 
	 * @return the text of this instruction
	 */
	@Override
	public String toString() {
		return text;
	}
}
