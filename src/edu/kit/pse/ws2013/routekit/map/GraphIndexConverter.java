package edu.kit.pse.ws2013.routekit.map;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts a {@link TreeGraphIndex} to an {@link ArrayGraphIndex}.
 */
public class GraphIndexConverter {

	private final List<Integer> contents = new ArrayList<>();
	private final List<Integer> nodes = new ArrayList<>();
	private final TreeGraphIndex index;

	public GraphIndexConverter(TreeGraphIndex index) {
		this.index = index;
		writeNode(index.root);
	}

	private int writeNode(TreeGraphIndex.Node node) {
		int ret = nodes.size();
		if (node.contents != null) {
			nodes.add(-1);
			nodes.add(contents.size());
			nodes.add(node.contents.size());
			contents.addAll(node.contents);
		} else {
			nodes.add(node.splitLat ? 1 : 0);
			nodes.add(0); // to be filled in later
			nodes.add(0); // to be filled in later
			nodes.add(Float.floatToIntBits(node.threshhold));
			nodes.set(ret + 1, writeNode(node.left));
			nodes.set(ret + 2, writeNode(node.right));
		}
		return ret;
	}

	public ArrayGraphIndex getIndex() {
		int[] contents = new int[this.contents.size()];
		int[] nodes = new int[this.nodes.size()];
		for (int i = 0; i < contents.length; i++) {
			contents[i] = this.contents.get(i);
		}
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = this.nodes.get(i);
		}
		return new ArrayGraphIndex(index.getGraph(), index.getView(), contents,
				nodes);
	}
}
