package spoon.test.constructorcallnewclass.testclasses;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class Panini {
	static final AtomicLong[] EMPTY = new AtomicLong[0];

	public void m() {
		final AtomicReference<AtomicLong[]> atomicReference = new AtomicReference<>(EMPTY);
	}
}
