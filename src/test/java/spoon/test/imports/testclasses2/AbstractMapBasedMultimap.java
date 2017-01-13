package spoon.test.imports.testclasses2;

abstract class AbstractMapBasedMultimap<K, V> {
	private class WrappedCollection {
		class WrappedIterator {
		}
	}
	private class WrappedList extends WrappedCollection {
		private class WrappedListIterator extends WrappedIterator {
		}
	}

	private class OtherWrappedList extends WrappedCollection {
		private class WrappedListIterator extends WrappedIterator {
		}

		class WrappedIterator {

		}
	}
}
