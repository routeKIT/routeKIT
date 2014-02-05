package edu.kit.pse.ws2013.routekit.map;

import java.awt.geom.Line2D;
import java.util.HashMap;

import edu.kit.pse.ws2013.routekit.util.Coordinates;

public class ReducedGraphView implements GraphView {

	int[] startnodes;
	int[] endnodes;
	int[] mapping;
	int edgepos = 0;

	public ReducedGraphView(Graph g, HighwayType maxType, int zoom) {
		int maxTypeId = maxType.ordinal();
		startnodes = new int[g.getNumberOfEdges()];
		endnodes = new int[g.getNumberOfEdges()];
		mapping = new int[g.getNumberOfEdges()];
		int[] degree = new int[g.getNumberOfNodes()];
		int[] outdegree = new int[g.getNumberOfNodes()];
		int[] indegree = new int[g.getNumberOfNodes()];
		boolean[] keep = new boolean[g.getNumberOfNodes()];
		boolean[] hide = new boolean[g.getNumberOfEdges()];

		for (int i = 0; i < g.getNumberOfEdges(); i++) {
			int correspondingEdge = g.getCorrespondingEdge(i);
			if (g.getEdgeProperties(i).getType().ordinal() > maxTypeId
					|| hide[i]) {
				hide[i] = true;
				if (correspondingEdge != -1) {
					hide[correspondingEdge] = true;
				}
				continue;
			}
			if (correspondingEdge < i) { // uniqe or not existent
				int start = g.getStartNode(i);
				int end = g.getTargetNode(i);
				outdegree[start]++;
				indegree[end]++;
				if (correspondingEdge != -1) {
					outdegree[end]++;
					indegree[start]++;
				}
				degree[start]++;
				degree[end]++;
			}
		}
		for (int i = 0; i < keep.length; i++) {
			if ((degree[i] == 2 && indegree[i] == 2 && outdegree[i] == 2)
					|| degree[i] == 0
					|| (degree[i] == 2 && indegree[i] == 1 && outdegree[i] == 1)) {
			} else {
				keep[i] = true;
			}
		}
		boolean[] done = new boolean[g.getNumberOfEdges()];
		Coordinates[] currentWay = new Coordinates[1024 * 4];
		int[] currentWayid = new int[currentWay.length];
		for (int i = 0; i < keep.length; i++) {
			if (!keep[i]) {
				continue;
			}
			for (Integer edge : g.getOutgoingEdges(i)) {
				int wp = 0;
				if (done[edge] || hide[edge]) {
					continue;
				}
				int target = g.getTargetNode(edge);
				// done[edge] = true;
				if (keep[target]) {
					addEdge(i, target, edge);
					continue;
				}
				currentWayid[wp] = i;
				currentWay[wp++] = g.getCoordinates(i);
				currentWayid[wp] = target;
				currentWay[wp++] = g.getCoordinates(target);
				int pretarget = i;
				int tedge = edge;
				while (!keep[target]) {
					int found = -1;
					for (Integer edge2 : g.getOutgoingEdges(target)) {
						if (g.getTargetNode(edge2) != pretarget && !hide[edge2]) {
							if (found != -1) {
								throw new Error();
							}
							found = g.getTargetNode(edge2);
							tedge = edge2;
						}
					}
					if (found == -1) {
						throw new Error();
					}
					pretarget = target;
					target = found;
					currentWayid[wp] = target;
					currentWay[wp++] = g.getCoordinates(target);
				}
				int rev = g.getCorrespondingEdge(tedge);
				if (rev != -1) {
					done[rev] = true;
				}
				boolean[] usedWay = new boolean[wp];
				douglasPeuker(currentWay, 0, wp, g, usedWay, zoom);
				int prev = currentWayid[0];
				for (int j = 1; j < wp; j++) {
					if (usedWay[j]) {
						int k = currentWayid[j];
						addEdge(prev, k, edge);
						prev = k;
					}
				}
			}
		}
		int[] out = new int[edgepos];
		System.arraycopy(startnodes, 0, out, 0, edgepos);
		startnodes = out;
		out = new int[edgepos];
		System.arraycopy(endnodes, 0, out, 0, edgepos);
		endnodes = out;
		out = new int[edgepos];
		System.arraycopy(mapping, 0, out, 0, edgepos);
		mapping = out;
	}

	private void addEdge(int start, int end, int org) {
		startnodes[edgepos] = start;
		endnodes[edgepos] = end;
		mapping[edgepos++] = org;

	}

	private void douglasPeuker(Coordinates[] currentWay, int startIdx,
			int endIdx, Graph g, boolean[] used, int zoom) {
		Coordinates start = currentWay[startIdx];
		Coordinates end = currentWay[endIdx - 1];
		double maxdist = -1;
		int maxidx = -1;
		for (int j = startIdx + 1; j < endIdx - 1; j++) {
			Coordinates c = currentWay[j];
			double dist = Line2D.ptLineDistSq(start.getSmtX(zoom),
					start.getSmtY(zoom), end.getSmtX(zoom), end.getSmtY(zoom),
					c.getSmtX(zoom), c.getSmtY(zoom));
			if (dist > maxdist) {
				maxdist = dist;
				maxidx = j;
			}
		}
		if (maxdist > 5f / 256f / 256f) {//
			douglasPeuker(currentWay, startIdx, maxidx, g, used, zoom);
			used[maxidx] = true;
			douglasPeuker(currentWay, maxidx, endIdx, g, used, zoom);
		} else {
			used[startIdx] = true;
			used[endIdx - 1] = true;
		}
	}

	public static void main(String[] args) {
		EdgeProperties ep = new EdgeProperties(HighwayType.Residential, "", "",
				10);
		Graph g = new Graph(new int[] { 0, 1, 2, 3, 4, 6 }, new int[] { 1, 2,
				3, 4, 3, 5, 4 }, new HashMap<Integer, NodeProperties>(),
				new EdgeProperties[] { ep, ep, ep, ep, ep, ep, ep },
				new float[] { 0, 1, 0.5f, 0, 0, 0 }, new float[] { 0, 1, 2, 3,
						4, 5 });
		new ReducedGraphView(g, HighwayType.Residential, 10);
	}

	@Override
	public int getNumberOfEdges() {
		return edgepos;
	}

	@Override
	public int getStartNode(int edge) {
		return startnodes[edge];
	}

	@Override
	public int getTargetNode(int edge) {
		return endnodes[edge];
	}

	@Override
	public int translate(int edg) {
		return mapping[edg];
	}
}
