package edu.kit.pse.ws2013.routekit.routecalculation;

import java.util.ArrayList;
import java.util.List;

public class FibonacciHeap {
	private FibonacciHeapEntry min = null;
	private int size = 0;

	public boolean isEmpty() {
		if (min == null) {
			return true;
		}
		return false;
	}

	/**
	 * Gibt die Größe des Heaps zurück. Nützlich fürs Debugging.
	 * 
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	public FibonacciHeapEntry add(int value, int priority) {
		FibonacciHeapEntry newEntry = new FibonacciHeapEntry(value, priority);
		min = merge(min, newEntry);
		size++;

		return newEntry;
	}

	public FibonacciHeapEntry deleteMin() {
		if (isEmpty()) {
			return null;
		}

		FibonacciHeapEntry minEntry = min;

		if (min.getNext() == min) {
			min = null;
		} else {
			min.getPrev().setNext(min.getNext());
			min.getNext().setPrev(min.getPrev());

			min = min.getNext();
		}

		size--;

		if (minEntry.getChild() != null) {
			FibonacciHeapEntry currentEntry = minEntry.getChild();
			do {
				currentEntry.setParent(null);
				currentEntry = currentEntry.getNext();
			} while (currentEntry != minEntry.getChild());
		}

		min = merge(min, minEntry.getChild());

		if (min == null) {
			// System.out.println("A");
			return minEntry;
		}

		List<FibonacciHeapEntry> table = new ArrayList<FibonacciHeapEntry>();
		List<FibonacciHeapEntry> queue = new ArrayList<FibonacciHeapEntry>();

		for (FibonacciHeapEntry currentEntry = min; queue.isEmpty()
				|| queue.get(0) != currentEntry; currentEntry = currentEntry
				.getNext()) {
			queue.add(currentEntry);
		}

		for (FibonacciHeapEntry currentEntry : queue) {
			while (true) {
				while (currentEntry.getDegree() >= table.size()) {
					table.add(null);
				}

				if (table.get(currentEntry.getDegree()) == null) {
					table.set(currentEntry.getDegree(), currentEntry);
					break;
				}

				FibonacciHeapEntry other = table.get(currentEntry.getDegree());
				table.set(currentEntry.getDegree(), null);

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

	public void decreaseKey(FibonacciHeapEntry entry, int newPriority) {
		entry.setPriority(newPriority);

		if (entry.getParent() != null
				&& entry.getPriority() <= entry.getParent().getPriority()) {
			cut(entry);
		}

		if (entry.getPriority() <= min.getPriority()) {
			min = entry;
		}
	}

	public FibonacciHeapEntry merge(FibonacciHeapEntry one,
			FibonacciHeapEntry two) {
		if (one == null && two == null) {
			return null;
		} else if (one != null && two == null) {
			return one;
		} else if (one == null && two != null) {
			return two;
		} else {
			FibonacciHeapEntry oneNext = one.getNext();

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

	private void cut(FibonacciHeapEntry entry) {
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