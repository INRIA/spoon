package spoon.test.reference.testclasses;

import java.util.Iterator;
import java.util.Map;

public class Panini<K, V> {
	Iterator<Map.Entry<K, V>> entryIterator() {
		return new Itr<Map.Entry<K, V>>() {
			@Override
			Map.Entry<K, V> output(K key, V value) {
				return null;
			}
		};
	}

	private abstract class Itr<T> implements Iterator<T> {
		@Override
		public T next() {
			return null;
		}

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public void remove() {

		}

		abstract T output(K key, V value);
	}
}
