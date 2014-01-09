package edu.kit.pse.ws2013.routekit.routecalculation;

import java.util.Collections;
import java.util.List;

/**
 * The description of a {@link Route}.
 */
public class RouteDescription {
	private Route route;
	private List<TurnInstruction> instructions;

	/**
	 * Creates a new {@code RouteDescription} with the given attributes.
	 * 
	 * @param route
	 *            the described route
	 * @param instructions
	 *            a list of turn instructions
	 */
	public RouteDescription(Route route, List<TurnInstruction> instructions) {
		this.route = route;
		this.instructions = instructions;
	}

	/**
	 * Returns the route this description is about.
	 * 
	 * @return the described route
	 */
	public Route getRoute() {
		return route;
	}

	/**
	 * Returns the turn instructions this description consists of.
	 * 
	 * @return the list of instructions
	 */
	public List<TurnInstruction> getInstructions() {
		return Collections.unmodifiableList(instructions);
	}
}
