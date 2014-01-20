package edu.kit.pse.ws2013.routekit.precalculation;

import edu.kit.pse.ws2013.routekit.map.EdgeBasedGraph;
import edu.kit.pse.ws2013.routekit.map.EdgeProperties;
import edu.kit.pse.ws2013.routekit.map.Graph;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.Weights;
import edu.kit.pse.ws2013.routekit.profiles.Profile;

public class EdgeWeighterImpl implements EdgeWeighter {

	private final static int AVERAGE_TRAFFICLIGHT_WAIT_TIME = 60;
	private final static int AVERAGE_TURN_TIME_RIGHT = 3;
	private final static int AVERAGE_TURN_TIME_LEFT = 5;
	private final static int AVERAGE_TURN_TIME_HALFRIGHT = 1;
	private final static int AVERAGE_TURN_TIME_HALFLEFT = 2;
	private final static int AVERAGE_TURN_TIME_STRAIGHT = 0;
	private final static int AVERAGE_TURN_TIME_NO_TURN = 0;
	private final static int AVERAGE_TURN_TIME_ROUNDABOUT_ENTRY = 5;
	private final static int AVERAGE_TURN_TIME_ROUNDABOUT_EXIT = 3;
	private final static int AVERAGE_TURN_TIME_ROUNDABOUT_NO_EXIT = 0;
	private final static int AVERAGE_TURN_TIME_MOTORWAY_JUNCTION = 7;

	@Override
	public void weightEdges(ProfileMapCombination combination) {
		long t1 = System.currentTimeMillis();
		Graph graph = combination.getStreetMap().getGraph();
		EdgeBasedGraph eGraph = combination.getStreetMap().getEdgeBasedGraph();
		Profile profile = combination.getProfile();
		int[] weightArray = new int[eGraph.getNumberOfTurns()];
		for (int edge = 0; edge < graph.getNumberOfEdges(); edge++) {
			EdgeProperties currentEdgeProps = graph.getEdgeProperties(edge);
			int startNode = graph.getStartNode(edge);
			int targetNode = graph.getTargetNode(edge);
			float edgeLength = graph.getCoordinates(startNode).distanceTo(
					graph.getCoordinates(targetNode));
			int baseTime = Math.round(edgeLength
					/ (currentEdgeProps.getMaxSpeed(profile) * 1000) * 3600);
			if (graph.getNodeProperties(targetNode).isTrafficLights()) {
				baseTime += AVERAGE_TRAFFICLIGHT_WAIT_TIME;
			}
			for (int turn : eGraph.getOutgoingTurns(edge)) {
				if (!eGraph.allowsTurn(turn, profile)) {
					weightArray[turn] = Integer.MAX_VALUE;
				} else {
					int turnTime = 0;
					switch (eGraph.getTurnType(turn)) {
					case RightTurn:
						turnTime = AVERAGE_TURN_TIME_RIGHT;
						break;
					case LeftTurn:
						turnTime = AVERAGE_TURN_TIME_LEFT;
						break;
					case HalfRightTurn:
						turnTime = AVERAGE_TURN_TIME_HALFRIGHT;
						break;
					case HalfLeftTurn:
						turnTime = AVERAGE_TURN_TIME_HALFLEFT;
						break;
					case StraightOn:
						turnTime = AVERAGE_TURN_TIME_STRAIGHT;
						break;
					case NoTurn:
						turnTime = AVERAGE_TURN_TIME_NO_TURN;
						break;
					case RoundaboutEntry:
						turnTime = AVERAGE_TURN_TIME_ROUNDABOUT_ENTRY;
						break;
					case RoundaboutExit:
						turnTime = AVERAGE_TURN_TIME_ROUNDABOUT_EXIT;
						break;
					case RoundaboutNoExit:
						turnTime = AVERAGE_TURN_TIME_ROUNDABOUT_NO_EXIT;
						break;
					case MotorwayJunction:
						turnTime = AVERAGE_TURN_TIME_MOTORWAY_JUNCTION;
						break;
					default:
						turnTime = 0;
					}
					weightArray[turn] = baseTime + turnTime;
				}
			}
		}
		long t2 = System.currentTimeMillis();
		int time = (int) (t2 - t1);
		combination.setWeights(new Weights(weightArray), time);
	}
}
