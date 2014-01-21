package edu.kit.pse.ws2013.routekit.map;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Set;

public class IntIntervalSet extends AbstractCollection<Integer> implements
		Set<Integer> {
	public class IntArrayIterator implements Iterator<Integer> {
		int pos = 0;

		@Override
		public boolean hasNext() {
			return pos < count;
		}

		@Override
		public Integer next() {
			return base + pos++;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	int base;
	int count;

	public IntIntervalSet(int base, int count) {
		this.base = base;
		this.count = count;
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
