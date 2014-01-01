package edu.kit.pse.ws2013.routekit.map;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Set;

public class IntArraySet extends AbstractCollection<Integer> implements
		Set<Integer> {
	public class IntArrayIterator implements Iterator<Integer> {
		int pos = 0;

		@Override
		public boolean hasNext() {
			return pos < count;
		}

		@Override
		public Integer next() {
			return data[base + pos++];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	int base;
	int count;
	int[] data;

	public IntArraySet(int base, int count, int[] data) {
		this.base = base;
		this.count = count;
		this.data = data;
	}

	@Override
	public int size() {
		return count;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new IntArrayIterator();
	}
}
