package spoon.test.fieldaccesses.testclasses.internal;

abstract class Bar {
	abstract static class Inner {
		public static int i;

		public static class SubInner {
			public static int j;
		}
		public enum KnownOrder {
			KNOWN_ORDER, UNKNOWN_ORDER
		}
	}
}
