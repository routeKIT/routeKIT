package edu.kit.pse.ws2013.routekit.routecalculation;

import java.util.ArrayList;
import java.util.List;

/**
 * The priority queue for {@link ArcFlagsDijkstra} and the precalculation.
 */
public class FibonacciHeap {
	private FibonacciHeapEntry min = null;
	private int size = 0;

	/**
	 * Check if the heap is empty.
	 * 
	 * @return true if the heap is empty, otherwise false
	 */
	public boolean isEmpty() {
		return min == null;
	}

	/**
	 * Returns the size of the heap.
	 * 
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Add an entry to the Heap.
	 * 
	 * @param value
	 *            the value of the entry
	 * @param priority
	 *            the priority of the entry
	 * @return the new entry
	 */
	public FibonacciHeapEntry add(final int value, final int priority) {
		final FibonacciHeapEntry newEntry = new FibonacciHeapEntry(value,
				priority);
		min = merge(min, newEntry);
		size++;

		return newEntry;
	}

	/**
	 * Removes the minimal element of the Heap.
	 * 
	 * @return the removed element
	 */
	public FibonacciHeapEntry deleteMin() {
		if (isEmpty()) {
			return null;
		}

		final FibonacciHeapEntry minEntry = min;

		if (min.getNext() == min) {
			min = null;
		} else {
			min.getPrev().setNext(min.getNext());
			min.getNext().setPrev(min.getPrev());

			min = min.getNext();
		}

		size--;

		final FibonacciHeapEntry child = minEntry.getChild();
		if (child != null) {
			FibonacciHeapEntry currentEntry = child;
			do {
				currentEntry.setParent(null);
				currentEntry = currentEntry.getNext();
			} while (currentEntry != child);
		}

		min = merge(min, minEntry.getChild());

		if (min == null) {
			// System.out.println("A");
			return minEntry;
		}

		// Consolidate
		final List<FibonacciHeapEntry> table = new ArrayList<FibonacciHeapEntry>();
		final List<FibonacciHeapEntry> queue = new ArrayList<FibonacciHeapEntry>();

		for (FibonacciHeapEntry currentEntry = min; queue.isEmpty()
				|| queue.get(0) != currentEntry; currentEntry = currentEntry
				.getNext()) {
			queue.add(currentEntry);
		}

		for (FibonacciHeapEntry currentEntry : queue) {
			while (true) {
				final int currentEntryDegree = currentEntry.getDegree();
				while (currentEntryDegree >= table.size()) {
					table.add(null);
				}

				final FibonacciHeapEntry other = table.get(currentEntryDegree);
				if (other == null) {
					table.set(currentEntryDegree, currentEntry);
					break;
				}

				table.set(currentEntryDegree, null);

				FibonacciHeapEntry currentMin;
				if (other.getPriority() < currentEntry.getPriority()) {
					currentMin = other;
				} else {
					currentMin = currentEntry;
				}

				FibonacciHeapEntry currentMax;
				if (currentMin == other) {
					currentMax = currentEntry;
				} else {
					currentMax = other;
				}

				currentMax.getNext().setPrev(currentMax.getPrev());
				currentMax.getPrev().setNext(currentMax.getNext());

				currentMax.setNext(currentMax);
				currentMax.setPrev(currentMax);

				currentMin.setChild(merge(currentMin.getChild(), currentMax));

				currentMax.setParent(currentMin);

				currentMax.setMarked(false);

				currentMin.increaseDegree();

				currentEntry = currentMin;
			}

			if (currentEntry.getPriority() <= min.getPriority()) {
				min = currentEntry;
			}
		}

		return minEntry;
	}

	/**
	 * Decreases the key of an entry.
	 * 
	 * @param entry
	 *            the entry
	 * @param newPriority
	 *            the new priority
	 */
	public void decreaseKey(final FibonacciHeapEntry entry,
			final int newPriority) {
		// set the new priority
		entry.setPriority(newPriority);

		// cut the element if the heap property is broken
		if (entry.getParent() != null
				&& entry.getPriority() <= entry.getParent().getPriority()) {
			cut(entry);
		}

		// the entry has the smalles priority
		if (entry.getPriority() <= min.getPriority()) {
			min = entry;
		}
	}

	/**
	 * Merges two heap entries.
	 * 
	 * @param one
	 *            entry one to merge
	 * @param two
	 *            entry two to merge
	 * @return the merged entry
	 */
	private FibonacciHeapEntry merge(final FibonacciHeapEntry one,
			final FibonacciHeapEntry two) {
		if (one == null && two == null) {
			return null;
		} else if (one != null && two == null) {
			return one;
		} else if (two != null && one == null) {
			return two;
		} else {
			final FibonacciHeapEntry oneNext = one.getNext();

			one.setNext(two.getNext());
			one.getNext().setPrev(one);
			two.setNext(oneNext);
			two.getNext().setPrev(two);

			if (one.getPriority() < two.getPriority()) {
				return one;
			} else {
				return two;
			}
		}
	}

	/**
	 * Cuts the given entry and recursively repeats it if the parent is marked.
	 * 
	 * @param entry
	 *            the entry to cut
	 */
	private void cut(final FibonacciHeapEntry entry) {
		entry.setMarked(false);

		if (entry.getParent() == null) {
			return;
		}

		if (entry.getNext() != entry) {
			entry.getNext().setPrev(entry.getPrev());
			entry.getPrev().setNext(entry.getNext());
		}

		if (entry.getParent().getChild() == entry) {
			if (entry.getNext() != entry) {
				entry.getParent().setChild(entry.getNext());
			} else {
				entry.getParent().setChild(null);
			}
		}

		entry.getParent().decreaseDegree();

		entry.setPrev(entry);
		entry.setNext(entry);

		merge(min, entry);

		if (entry.getParent().isMarked()) {
			cut(entry.getParent());
		} else {
			entry.getParent().setMarked(true);
		}

		entry.setParent(null);
	}
}