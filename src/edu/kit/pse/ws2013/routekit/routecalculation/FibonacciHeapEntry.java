package edu.kit.pse.ws2013.routekit.routecalculation;

public class FibonacciHeapEntry {
	private int degree = 0;
	private boolean marked = false;

	private FibonacciHeapEntry parent;
	private FibonacciHeapEntry child;
	private FibonacciHeapEntry next;
	private FibonacciHeapEntry prev;

	private int value;
	private int priority;

	/**
	 * Konstruktor für FibonacciHeapEntry
	 * 
	 * @param value
	 *            Wer des Eintrags
	 * @param priority
	 *            Priorität des Eintrags
	 */
	public FibonacciHeapEntry(int value, int priority) {
		this.value = value;
		this.priority = priority;
		this.next = this;
		this.prev = this;
	}

	public void increaseDegree() {
		degree++;
	}

	public void decreaseDegree() {
		degree--;
	}

	/**
	 * @return the degree
	 */
	public int getDegree() {
		return degree;
	}

	/**
	 * @param degree
	 *            the degree to set
	 */
	public void setDegree(int degree) {
		this.degree = degree;
	}

	/**
	 * @return the marked
	 */
	public boolean isMarked() {
		return marked;
	}

	/**
	 * @param marked
	 *            the marked to set
	 */
	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	/**
	 * @return the parent
	 */
	public FibonacciHeapEntry getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(FibonacciHeapEntry parent) {
		this.parent = parent;
	}

	/**
	 * @return the child
	 */
	public FibonacciHeapEntry getChild() {
		return child;
	}

	/**
	 * @param child
	 *            the child to set
	 */
	public void setChild(FibonacciHeapEntry child) {
		this.child = child;
	}

	/**
	 * @return the next
	 */
	public FibonacciHeapEntry getNext() {
		return next;
	}

	/**
	 * @param next
	 *            the next to set
	 */
	public void setNext(FibonacciHeapEntry next) {
		this.next = next;
	}

	/**
	 * @return the prev
	 */
	public FibonacciHeapEntry getPrev() {
		return prev;
	}

	/**
	 * @param prev
	 *            the prev to set
	 */
	public void setPrev(FibonacciHeapEntry prev) {
		this.prev = prev;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
}