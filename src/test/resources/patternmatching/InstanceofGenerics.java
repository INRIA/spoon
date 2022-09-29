import java.util.Collection;
import java.util.List;
import java.util.Set;

class InstanceofGenerics {
	<T> boolean check(Iterable<T> iterable) {
		return iterable instanceof Set<T>;
	}

	<T> T first(Iterable<T> iterable) {
		if (iterable instanceof List<T> list) {
			return list.get(0);
		}
		return iterable.iterator().next();
	}

	interface StringCollection extends Collection<String> {}

	boolean alwaysTrue0(StringCollection collection) {
		return collection instanceof Collection<String>;
	}

	<T> boolean alwaysTrue1(Iterable<List<T>> iterable) {
		return iterable instanceof List<List<T>>;
	}

	<T> boolean alwaysTrue2(Iterable<List<? extends T>> iterable) {
		return iterable instanceof List<List<? extends T>>;
	}

	<T> boolean alwaysTrue3(Iterable<List<? super T>> iterable) {
		return iterable instanceof List<List<? super T>>;
	}
}
