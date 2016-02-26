package spoon.test.fieldaccesses.testclasses;

import spoon.test.fieldaccesses.testclasses.internal.Foo;

public class Tacos {
	public void m() {
		inv(Foo.i);
		inv(spoon.test.fieldaccesses.testclasses.internal.Foo.i);
		inv(Foo.SubInner.j);
		inv(spoon.test.fieldaccesses.testclasses.internal.Foo.SubInner.j);
		runIteratorTest(Foo.KnownOrder.KNOWN_ORDER);
		runIteratorTest(spoon.test.fieldaccesses.testclasses.internal.Foo.KnownOrder.KNOWN_ORDER);
	}

	private void runIteratorTest(spoon.test.fieldaccesses.testclasses.internal.Foo.KnownOrder knownOrder) {
	}

	private void inv(Foo.SubInner foo) {

	}

	private void inv(int i) {
	}

	private static class Burritos {
		public boolean add(java.lang.Object e) {
			throw new java.lang.UnsupportedOperationException();
		}
	}
}
