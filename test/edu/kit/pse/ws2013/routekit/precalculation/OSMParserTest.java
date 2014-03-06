package edu.kit.pse.ws2013.routekit.precalculation;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXParseException;

import edu.kit.pse.ws2013.routekit.map.EdgeBasedGraph;
import edu.kit.pse.ws2013.routekit.map.EdgeProperties;
import edu.kit.pse.ws2013.routekit.map.Graph;
import edu.kit.pse.ws2013.routekit.map.HighwayType;
import edu.kit.pse.ws2013.routekit.map.NodeProperties;
import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.map.TurnType;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;

public class OSMParserTest {
	private static final float EPSILON = 1E-6F;

	private ProgressReporter reporter;
	private OSMParser parser;

	@Before
	public void setUp() {
		reporter = new ProgressReporter();
		reporter.pushTask("Dummy");

		parser = new OSMParser();
	}

	private static File getTestFile(String name) {
		return new File(OSMParserTest.class.getResource(name).getFile());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFileNull() throws Exception {
		parser.parseOSM(null, reporter);
	}

	/*
	 * @Test(expected = SAXParseException.class) public void testNoOSMFile()
	 * throws Exception { parser.parseOSM(getTestFile("testNoOSM.xml"),
	 * reporter); }
	 */

	@Test(expected = SAXParseException.class)
	public void testMissingTagName() throws Exception {
		parser.parseOSM(getTestFile("testMissingTagName.osm"), reporter);
	}

	@Test(expected = SAXParseException.class)
	public void testMissingTagValue() throws Exception {
		parser.parseOSM(getTestFile("testMissingTagValue.osm"), reporter);
	}

	@Test(expected = SAXParseException.class)
	public void testMissingTagNameNode() throws Exception {
		parser.parseOSM(getTestFile("testMissingTagNameNode.osm"), reporter);
	}

	@Test(expected = SAXParseException.class)
	public void testMissingTagValueNode() throws Exception {
		parser.parseOSM(getTestFile("testMissingTagValueNode.osm"), reporter);
	}

	@Test(expected = SAXParseException.class)
	public void testMissingCoordinates() throws Exception {
		parser.parseOSM(getTestFile("testMissingCoordinates.osm"), reporter);
	}

	@Test(expected = SAXParseException.class)
	public void testInvalidCoordinates() throws Exception {
		parser.parseOSM(getTestFile("testInvalidCoordinates.osm"), reporter);
	}

	@Test(expected = SAXParseException.class)
	public void testInvalidNodeReference() throws Exception {
		parser.parseOSM(getTestFile("testInvalidNodeReference.osm"), reporter);
	}

	@Test(expected = SAXParseException.class)
	public void testMissingNodeId() throws Exception {
		parser.parseOSM(getTestFile("testMissingNodeID.osm"), reporter);
	}

	@Test(expected = SAXParseException.class)
	public void testInvalidWayId() throws Exception {
		parser.parseOSM(getTestFile("testInvalidWayID.osm"), reporter);
	}

	@Test(expected = SAXParseException.class)
	public void testInvalidWayMemberReference() throws Exception {
		parser.parseOSM(getTestFile("testInvalidWayMemberReference.osm"),
				reporter);
	}

	@Test(expected = SAXParseException.class)
	public void testMissingNodeMemberReference() throws Exception {
		parser.parseOSM(getTestFile("testMissingNodeMemberReference.osm"),
				reporter);
	}

	@Test
	public void testWay() throws Exception {
		StreetMap map = parser.parseOSM(getTestFile("testWay.osm"), reporter);
		Graph graph = map.getGraph();
		EdgeBasedGraph ebg = map.getEdgeBasedGraph();

		assertEquals(4, graph.getNumberOfEdges());
		assertEquals(3, graph.getNumberOfNodes());

		EdgeProperties edgeProps = graph.getEdgeProperties(0);
		assertEquals("Hauptstraße", edgeProps.getName());
		assertEquals("B 42", edgeProps.getRoadRef());
		assertEquals(HighwayType.Primary, edgeProps.getType());
		for (int i = 1; i < graph.getNumberOfEdges(); i++) {
			assertSame(edgeProps, graph.getEdgeProperties(i));
		}

		float[] lat = { 48.910534999324206F, 48.910534999324206F,
				48.91543512675681F };
		float[] lon = { 8.547609656760535F, 8.557401293357438F,
				8.561803038249623F };
		int[] nodes = new int[3];
		Arrays.fill(nodes, -1);
		for (int i = 0; i < lat.length; i++) {
			for (int j = 0; j < nodes.length; j++) {
				if (Math.abs(lat[i] - graph.getCoordinates(j).getLatitude()) < EPSILON
						&& Math.abs(lon[i]
								- graph.getCoordinates(j).getLongitude()) < EPSILON) {
					nodes[i] = j;
				}
			}
			if (nodes[i] == -1) {
				fail();
			}
		}

		assertEquals(1, graph.getOutgoingEdges(nodes[0]).size());
		assertEquals(2, graph.getOutgoingEdges(nodes[1]).size());
		assertEquals(1, graph.getOutgoingEdges(nodes[2]).size());
		int[] edges = new int[4];
		edges[0] = graph.getOutgoingEdges(nodes[0]).iterator().next();
		edges[2] = graph.getOutgoingEdges(nodes[2]).iterator().next();
		for (int edge : graph.getOutgoingEdges(nodes[1])) {
			if (graph.getTargetNode(edge) == nodes[0]) {
				edges[3] = edge;
			} else if (graph.getTargetNode(edge) == nodes[2]) {
				edges[1] = edge;
			} else {
				fail();
			}
		}
		assertFalse(edges[1] == edges[3]);

		assertEquals(nodes[0], graph.getTargetNode(edges[3]));
		assertEquals(nodes[1], graph.getTargetNode(edges[2]));
		assertEquals(nodes[2], graph.getTargetNode(edges[1]));
		assertEquals(nodes[1], graph.getTargetNode(edges[0]));

		assertEquals(2, ebg.getNumberOfTurns());
		assertTrue(ebg.getIncomingTurns(edges[0]).isEmpty());
		assertTrue(ebg.getOutgoingTurns(edges[1]).isEmpty());
		assertTrue(ebg.getIncomingTurns(edges[2]).isEmpty());
		assertTrue(ebg.getOutgoingTurns(edges[3]).isEmpty());
		assertEquals(1, ebg.getIncomingTurns(edges[1]).size());
		assertEquals(1, ebg.getIncomingTurns(edges[3]).size());
		assertEquals(ebg.getIncomingTurns(edges[1]),
				ebg.getOutgoingTurns(edges[0]));
		assertEquals(ebg.getIncomingTurns(edges[3]),
				ebg.getOutgoingTurns(edges[2]));

		int turn = ebg.getOutgoingTurns(edges[0]).iterator().next();
		assertEquals(TurnType.NoTurn, ebg.getTurnType(turn));
		assertEquals(edges[1], ebg.getTargetEdge(turn));

		turn = ebg.getOutgoingTurns(edges[2]).iterator().next();
		assertEquals(TurnType.NoTurn, ebg.getTurnType(turn));
		assertEquals(edges[3], ebg.getTargetEdge(turn));
	}

	@Test
	public void testOneway() throws Exception {
		StreetMap map = parser
				.parseOSM(getTestFile("testOneway.osm"), reporter);
		Graph graph = map.getGraph();

		assertEquals(1, graph.getNumberOfEdges());
		assertEquals(8.547609656760535,
				graph.getCoordinates(graph.getStartNode(0)).getLongitude(),
				EPSILON);
		assertEquals(8.557401293357438,
				graph.getCoordinates(graph.getTargetNode(0)).getLongitude(),
				EPSILON);
	}

	@Test
	public void testOnewayBackwards() throws Exception {
		StreetMap map = parser.parseOSM(getTestFile("testOnewayBackwards.osm"),
				reporter);
		Graph graph = map.getGraph();

		assertEquals(1, graph.getNumberOfEdges());
		assertEquals(8.557401293357438,
				graph.getCoordinates(graph.getStartNode(0)).getLongitude(),
				EPSILON);
		assertEquals(8.547609656760535,
				graph.getCoordinates(graph.getTargetNode(0)).getLongitude(),
				EPSILON);
	}

	@Test
	public void testTrafficSignals() throws Exception {
		Graph graph = parser.parseOSM(getTestFile("testTrafficSignals.osm"),
				reporter).getGraph();
		assertTrue(graph.getNodeProperties(graph.getStartNode(0))
				.isTrafficLights());
	}

	private void testTurn(String file, TurnType type) throws Exception {
		StreetMap map = parser.parseOSM(getTestFile(file), reporter);
		Graph graph = map.getGraph();
		EdgeBasedGraph ebg = map.getEdgeBasedGraph();

		assertEquals(2, ebg.getNumberOfTurns());
		boolean found = false;
		for (int turn = 0; turn < ebg.getNumberOfTurns(); turn++) {
			if (graph.getEdgeProperties(ebg.getTargetEdge(turn)).getType() == HighwayType.Secondary) {
				assertEquals(type, ebg.getTurnType(turn));
				found = true;
			}
		}
		assertTrue(found);
	}

	@Test
	public void testRightTurn() throws Exception {
		testTurn("testRightTurn.osm", TurnType.RightTurn);
	}

	@Test
	public void testHalfRightTurn() throws Exception {
		testTurn("testHalfRightTurn.osm", TurnType.HalfRightTurn);
	}

	@Test
	public void testLeftTurn() throws Exception {
		testTurn("testLeftTurn.osm", TurnType.LeftTurn);
	}

	@Test
	public void testHalfLeftTurn() throws Exception {
		testTurn("testHalfLeftTurn.osm", TurnType.HalfLeftTurn);
	}

	@Test
	public void testStraightOn() throws Exception {
		testTurn("testStraightOn.osm", TurnType.StraightOn);
	}

	@Test
	public void testMotorwayJunction() throws Exception {
		StreetMap map = parser.parseOSM(
				getTestFile("testMotorwayJunction.osm"), reporter);
		Graph graph = map.getGraph();
		EdgeBasedGraph ebg = map.getEdgeBasedGraph();

		assertEquals(2, ebg.getNumberOfTurns());
		NodeProperties nodeProps = graph.getNodeProperties(graph
				.getStartNode(ebg.getTargetEdge(0)));
		assertTrue(nodeProps.isMotorwayJunction());
		assertEquals("Buxtehude West", nodeProps.getJunctionName());
		assertEquals("42", nodeProps.getJunctionRef());

		boolean found = false;
		for (int turn = 0; turn < ebg.getNumberOfTurns(); turn++) {
			if (graph.getEdgeProperties(ebg.getTargetEdge(turn)).getRoadRef() == null) {
				assertEquals(TurnType.MotorwayJunction, ebg.getTurnType(turn));
				found = true;
			} else {
				assertEquals(TurnType.StraightOn, ebg.getTurnType(turn));
			}
		}
		assertTrue(found);
	}

	@Test
	public void testRoundabout() throws Exception {
		StreetMap map = parser.parseOSM(getTestFile("testRoundabout.osm"),
				reporter);
		Graph graph = map.getGraph();
		EdgeBasedGraph ebg = map.getEdgeBasedGraph();

		assertEquals(5, graph.getNumberOfEdges());
		assertEquals(5, ebg.getNumberOfTurns());
		int exitNode = -1;
		for (int node = 0; node < graph.getNumberOfNodes(); node++) {
			if (graph.getOutgoingEdges(node).size() == 2) {
				exitNode = node;
				break;
			}
		}
		assertFalse(exitNode < 0);

		for (int edge : graph.getOutgoingEdges(exitNode)) {
			Set<Integer> turns = ebg.getIncomingTurns(edge);
			switch (turns.size()) {
			case 1:
				assertEquals(TurnType.RoundaboutExit,
						ebg.getTurnType(turns.iterator().next()));
				break;
			case 2:
				for (int turn : turns) {
					TurnType type = ebg.getTurnType(turn);
					if (graph.getEdgeProperties(ebg.getStartEdge(turn))
							.getName() != null) {
						assertEquals(TurnType.RoundaboutNoExit, type);
					} else {
						assertEquals(TurnType.RoundaboutEntry, type);
					}
				}
				break;
			default:
				fail();
			}
		}

		int noTurns = 0;
		for (int turn = 0; turn < ebg.getNumberOfTurns(); turn++) {
			if (ebg.getTurnType(turn) == TurnType.NoTurn) {
				noTurns++;
			}
		}
		assertEquals(2, noTurns);
	}

	@Test
	public void testRestrictionNo() throws Exception {
		StreetMap map = parser.parseOSM(getTestFile("testRestrictionNo.osm"),
				reporter);
		Graph graph = map.getGraph();
		EdgeBasedGraph ebg = map.getEdgeBasedGraph();

		assertEquals(2, ebg.getNumberOfTurns());
		for (int edge = 0; edge < graph.getNumberOfEdges(); edge++) {
			if (graph.getEdgeProperties(edge).getType() == HighwayType.Secondary) {
				assertEquals(1, ebg.getIncomingTurns(edge).size());
			}
		}
	}

	@Test
	public void testRestrictionOnly() throws Exception {
		StreetMap map = parser.parseOSM(getTestFile("testRestrictionOnly.osm"),
				reporter);
		Graph graph = map.getGraph();
		EdgeBasedGraph ebg = map.getEdgeBasedGraph();

		assertEquals(2, ebg.getNumberOfTurns());
		for (int turn = 0; turn < ebg.getNumberOfTurns(); turn++) {
			assertFalse(graph.getEdgeProperties(ebg.getTargetEdge(turn))
					.getType() == HighwayType.Primary);
		}
	}

	@Test
	public void testNdTagOutsideWay() throws Exception {
		Graph graph = parser.parseOSM(getTestFile("testNdTagOutsideWay.osm"),
				reporter).getGraph();
		assertEquals(4, graph.getNumberOfEdges());
		assertEquals(3, graph.getNumberOfNodes());
	}

	@Test
	public void testMemberTagOutsideRelation() throws Exception {
		Graph graph = parser.parseOSM(
				getTestFile("testMemberTagOutsideRelation.osm"), reporter)
				.getGraph();
		assertEquals(4, graph.getNumberOfEdges());
		assertEquals(3, graph.getNumberOfNodes());
	}

	@Test
	public void testOtherHighway() throws Exception {
		Graph graph = parser.parseOSM(getTestFile("testOtherHighway.osm"),
				reporter).getGraph();
		assertEquals(0, graph.getNumberOfEdges());
	}
}
