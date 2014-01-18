package edu.kit.pse.ws2013.routekit.routecalculation;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import edu.kit.pse.ws2013.routekit.map.Graph;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.util.Coordinates;
import edu.kit.pse.ws2013.routekit.util.PointOnEdge;

/**
 * A calculated route.
 */
public class Route {
	private ProfileMapCombination data;
	private PointOnEdge start;
	private PointOnEdge destination;
	private List<Integer> turns;

	/**
	 * Creates a new {@code Route} object with the given attributes.
	 * 
	 * @param data
	 *            the map data on which the route was calculated
	 * @param start
	 *            the start point of the route
	 * @param destination
	 *            the destination point of the route
	 * @param turns
	 *            a list of turns the route consists of
	 */
	public Route(ProfileMapCombination data, PointOnEdge start,
			PointOnEdge destination, List<Integer> turns) {
		this.data = data;
		this.start = start;
		this.destination = destination;
		this.turns = turns;
	}

	/**
	 * Returns the map data on which this route was calculated.
	 * 
	 * @return the map data
	 */
	public ProfileMapCombination getData() {
		return data;
	}

	/**
	 * Returns the start point of this route.
	 * 
	 * @return the start point
	 */
	public PointOnEdge getStart() {
		return start;
	}

	/**
	 * Returns the destination point of this route.
	 * 
	 * @return the destination point
	 */
	public PointOnEdge getDestination() {
		return destination;
	}

	/**
	 * Returns the turns which this route consists of.
	 * 
	 * @return the list of turns
	 */
	public List<Integer> getTurns() {
		return Collections.unmodifiableList(turns);
	}

	/**
	 * Returns an iterator over the coordinates of the route points, including
	 * the start and destination point. The iterator are determined dynamically
	 * from the list of turns.
	 * 
	 * @return the said iterator
	 */
	public Iterator<Coordinates> getWaypointIterator() {
		return new Iterator<Coordinates>() {
			private int item = -2;

			@Override
			public boolean hasNext() {
				return item < turns.size();
			}

			@Override
			public Coordinates next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				item++;
				if (item == -1) {
					return getCoordinatesFromPoint(start);
				}
				if (item == turns.size()) {
					return getCoordinatesFromPoint(destination);
				}
				Graph graph = data.getStreetMap().getGraph();
				return graph.getCoordinates(graph.getTargetNode(data
						.getStreetMap().getEdgeBasedGraph()
						.getStartEdge(turns.get(item))));
			}

			private Coordinates getCoordinatesFromPoint(PointOnEdge point) {
				Graph graph = data.getStreetMap().getGraph();
				Coordinates start = graph.getCoordinates(graph
						.getStartNode(point.getEdge()));
				Coordinates target = graph.getCoordinates(graph
						.getTargetNode(point.getEdge()));
				return start.goIntoDirection(target, point.getPosition());
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
