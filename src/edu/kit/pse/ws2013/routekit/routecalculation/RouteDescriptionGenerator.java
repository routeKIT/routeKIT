package edu.kit.pse.ws2013.routekit.routecalculation;

import java.util.ArrayList;
import java.util.List;

import edu.kit.pse.ws2013.routekit.map.EdgeBasedGraph;
import edu.kit.pse.ws2013.routekit.map.EdgeProperties;
import edu.kit.pse.ws2013.routekit.map.Graph;
import edu.kit.pse.ws2013.routekit.map.NodeProperties;

/**
 * Provides the functionality for generating a {@link RouteDescription}.
 */
public class RouteDescriptionGenerator {
	private final static String[] ordinalNumbers = { "erste", "zweite",
			"dritte", "vierte", "fünfte", "sechste", "siebte", "achte",
			"neunte", "zehnte", "elfte", "zwölfte" };

	/**
	 * Generates the route description for the given {@link Route}.
	 * 
	 * @param route
	 *            the route to be described
	 * @return the generated {@link RouteDescription}
	 * @throws IllegalArgumentException
	 *             if {@code route} is {@code null}
	 */
	public RouteDescription generateRouteDescription(Route route) {
		if (route == null) {
			throw new IllegalArgumentException();
		}

		List<TurnInstruction> instructions = new ArrayList<>();
		Graph graph = route.getData().getStreetMap().getGraph();
		EdgeBasedGraph edgeBasedGraph = route.getData().getStreetMap()
				.getEdgeBasedGraph();

		int roundaboutExit = 0;
		for (int turn : route.getTurns()) {
			StringBuilder instr = null;
			switch (edgeBasedGraph.getTurnType(turn)) {
			case StraightOn:
				instr = new StringBuilder("Geradeaus weiterfahren");
				break;
			case RightTurn:
				instr = new StringBuilder("Rechts abbiegen");
				break;
			case HalfRightTurn:
				instr = new StringBuilder("Rechts halten");
				break;
			case LeftTurn:
				instr = new StringBuilder("Links abbiegen");
				break;
			case HalfLeftTurn:
				instr = new StringBuilder("Links halten");
				break;
			case MotorwayJunction:
				instr = new StringBuilder("An der Ausfahrt");
				NodeProperties junctionProps = graph.getNodeProperties(graph
						.getTargetNode(edgeBasedGraph.getStartEdge(turn)));
				String junctionName = junctionProps.getJunctionName();
				String junctionRef = junctionProps.getJunctionRef();
				if (junctionName != null) {
					instr.append(' ').append(junctionName);
					if (junctionRef != null) {
						instr.append(" (Nr. ").append(junctionRef).append(')');
					}
				} else if (junctionRef != null) {
					instr.append(" Nr. ").append(junctionRef);
				}
				instr.append(" abfahren");
				break;
			case RoundaboutEntry:
				roundaboutExit = 0;
				break;
			case RoundaboutNoExit:
				roundaboutExit++;
				break;
			case RoundaboutExit:
				instr = new StringBuilder("Im Kreisverkehr die ");
				if (roundaboutExit < 12) {
					instr.append(ordinalNumbers[roundaboutExit]);
				} else {
					instr.append(roundaboutExit).append('.');
				}
				instr.append(" Ausfahrt nehmen");
				break;
			default:
				break;
			}
			if (instr != null) {
				EdgeProperties props = graph.getEdgeProperties(edgeBasedGraph
						.getTargetEdge(turn));
				CharSequence target = displayName(props.getName(),
						props.getRoadRef());
				if (target != null) {
					instr.append(" auf ").append(target);
				}
				instructions.add(new TurnInstruction(turn, instr.toString()));
			}
		}

		return new RouteDescription(route, instructions);
	}

	private CharSequence displayName(String name, String ref) {
		if (name != null && ref != null) {
			return new StringBuilder(name).append(" (").append(ref).append(")");
		}
		return (name == null) ? ref : name;
	}
}
