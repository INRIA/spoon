package spoon.test.reference;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

abstract class TypeParameterErasureRecursion<W, U> extends AbstractSet<U> {
	private final Set<W> values;

	TypeParameterErasureRecursion(Set<W> values) {
		this.values = values;
	}

	@Override
	public Iterator<U> iterator() {
		return new Iterator<>() {
			private final Iterator<W> iterator = values.iterator();

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public U next() {
				return unwrap(iterator.next());
			}
		};
	}

	protected abstract U unwrap(W value);

	@Override
	@SuppressWarnings("unchecked")
	public <A> A[] toArray(A[] array) {
		Object[] result = array;
		for (U value : this) {
			result[0] = value;
		}
		return array;
	}
}

abstract class GenericMap<E> {
	private final Map<String, E> delegate = null;

	Set<Map.Entry<String, E>> entrySet() {
		return new TypeParameterErasureRecursion<>(delegate.entrySet()) {
			@Override
			protected Map.Entry<String, E> unwrap(Map.Entry<String, E> value) {
				return value;
			}
		};
	}
}

class GenericLambda {
	static <T> void mapAll(Collection<? extends T> values, Function<T, MissingKey> mapper) {
		values.forEach(value -> consume(mapper.apply(value), value));
	}

	static <T> void consume(MissingKey key, T value) {
	}
}
