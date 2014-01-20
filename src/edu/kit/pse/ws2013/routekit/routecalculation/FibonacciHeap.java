package edu.kit.pse.ws2013.routekit.routecalculation;

import java.util.ArrayList;
import java.util.List;

public class FibonacciHeap {
	private Entry min = null;
	private int size = 0;

	public boolean isEmpty() {
		if (min == null) {
			return true;
		}
		return false;
	}

	public Entry add(int value, int priority) {
		Entry newEntry = new Entry(value, priority);
		min = merge(min, newEntry);
		size++;

		return newEntry;
	}

	public Entry deleteMin() {
		if (isEmpty()) {
			return null;
		}

		Entry minEntry = min;

		if (min.next == min) {
			min = null;
		} else {
			min.prev.next = min.next;
			min.next.prev = min.prev;
			min = min.next;
		}

		size--;

		if (minEntry.child != null) {
			Entry currentEntry = minEntry.child;
			do {
				currentEntry.parent = null;
				currentEntry = currentEntry.next;
			} while (currentEntry != minEntry.child);
		}

		min = merge(min, minEntry.child);

		if (min == null) {
			// System.out.println("A");
			return minEntry;
		}

		List<Entry> table = new ArrayList<Entry>();
		List<Entry> queue = new ArrayList<Entry>();

		for (Entry currentEntry = min; queue.isEmpty()
				|| queue.get(0) != currentEntry; currentEntry = currentEntry.next) {
			queue.add(currentEntry);
		}

		for (Entry currentEntry : queue) {
			while (true) {
				while (currentEntry.degree >= table.size()) {
					table.add(null);
				}

				if (table.get(currentEntry.degree) == null) {
					table.set(currentEntry.degree, currentEntry);
					break;
				}

				Entry other = table.get(currentEntry.degree);
				table.set(currentEntry.degree, null);

				Entry currentMin;
				if (other.priority < currentEntry.priority) {
					currentMin = other;
				} else {
					currentMin = currentEntry;
				}

				Entry currentMax;
				if (currentMin == other) {
					currentMax = currentEntry;
				} else {
					currentMax = other;
				}

				currentMax.next.prev = currentMax.prev;
				currentMax.prev.next = currentMax.next;

				currentMax.next = currentMax;
				currentMax.prev = currentMax;

				currentMin.child = merge(currentMin.child, currentMax);

				currentMax.parent = currentMin;

				currentMax.marked = false;

				currentMin.degree++;

				currentEntry = currentMin;
			}

			if (currentEntry.priority <= min.priority) {
				min = currentEntry;
			}
		}

		return minEntry;
	}

	public void decreaseKey(Entry entry, int newPriority) {
		entry.priority = newPriority;

		if (entry.parent != null && entry.priority <= entry.parent.priority) {
			cut(entry);
		}

		if (entry.priority <= min.priority) {
			min = entry;
		}
	}

	public Entry merge(Entry one, Entry two) {
		if (one == null && two == null) {
			return null;
		} else if (one != null && two == null) {
			return one;
		} else if (one == null && two != null) {
			return two;
		} else {
			Entry oneNext = one.next;

			one.next = two.next;
			one.next.prev = one;
			two.next = oneNext;
			two.next.prev = two;

			if (one.priority < two.priority) {
				return one;
			} else {
				return two;
			}
		}
	}

	private void cut(Entry entry) {
		entry.marked = false;

		if (entry.parent == null) {
			return;
		}

		if (entry.next != entry) {
			entry.next.prev = entry.prev;
			entry.prev.next = entry.next;
		}

		if (entry.parent.child == entry) {
			if (entry.next != entry) {
				entry.parent.child = entry.next;
			} else {
				entry.parent.child = null;
			}
		}

		entry.parent.degree--;

		entry.prev = entry;
		entry.next = entry;

		merge(min, entry);

		if (entry.parent.marked) {
			cut(entry.parent);
		} else {
			entry.parent.marked = true;
		}

		entry.parent = null;
	}

	// Klasse für einen Eintrag
	public class Entry {
		private int degree = 0;
		private boolean marked = false;

		private Entry parent;
		private Entry child;
		private Entry next;
		private Entry prev;

		private int value;
		private int priority;

		private Entry(int value, int priority) {
			this.value = value;
			this.priority = priority;
			this.next = this;
			this.prev = this;
		}

		public int getValue() {
			return value;
		}

		public int getPriority() {
			return priority;
		}
	}
}