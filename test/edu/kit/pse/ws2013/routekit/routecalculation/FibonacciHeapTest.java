package edu.kit.pse.ws2013.routekit.routecalculation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FibonacciHeapTest {
	FibonacciHeap fh;

	@Before
	public void setUp() throws Exception {
		fh = new FibonacciHeap();
	}

	@After
	public void tearDown() throws Exception {
		fh = null;
	}

	@Test
	public void addTest() {
		fh.add(10, 2);
		fh.add(800, 10);
		fh.add(50, 1);
		fh.add(100, 5);

		assertEquals(fh.deleteMin().getValue(), 50);
		assertEquals(fh.deleteMin().getValue(), 10);
	}

	@Test
	public void decreaseTest() {
		fh.add(10, 3);
		FibonacciHeapEntry dec = fh.add(800, 10);
		fh.add(50, 1);
		fh.add(100, 5);

		fh.decreaseKey(dec, 2);

		assertEquals(fh.deleteMin().getValue(), 50);
		assertEquals(fh.deleteMin().getValue(), 800);
	}

	@Test
	public void removeTest() {
		fh.add(10, 3);
		fh.add(50, 1);
		fh.add(100, 5);

		assertEquals(fh.deleteMin().getValue(), 50);
		assertEquals(fh.deleteMin().getValue(), 10);
		// fh.deleteMin();
		// fh.deleteMin();
		assertEquals(fh.deleteMin().getValue(), 100);
	}

	@Test
	public void mixedTest() {
		fh.add(10, 3);
		fh.add(50, 2);
		FibonacciHeapEntry fhe1 = fh.add(100, 5);
		fh.add(299, 8);

		assertEquals(fh.deleteMin().getValue(), 50);

		fh.decreaseKey(fhe1, 1);
		assertEquals(fh.deleteMin().getValue(), 100);
		assertEquals(fh.deleteMin().getValue(), 10);
		assertEquals(fh.deleteMin().getValue(), 299);
	}

	@Test
	public void sizeTest() {
		fh.add(1, 1);

		assertEquals(fh.getSize(), 1);
	}

	@Test
	public void cutTest() {
		List<FibonacciHeapEntry> fhList = new ArrayList<FibonacciHeapEntry>();

		for (int i = 1; i <= 50; i++) {
			fhList.add(fh.add(i, i + 1));
		}

		fhList.remove(fh.deleteMin());
		// fhList.remove(fh.deleteMin());

		for (FibonacciHeapEntry entry : fhList) {
			fh.decreaseKey(entry, 1);
		}
	}

}
