package spoon.test.generics;

public class Foo<K, V> {

	protected Bar<K, V> meth1() {
		return new Bar<K, V>() {
			public V transform(final K input) {
				if (input instanceof String) {
					return (V) "NULL";
				}
				return (V) "NULL_OBJECT";
			}
		};
	}

}
