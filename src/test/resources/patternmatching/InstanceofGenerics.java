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

	boolean alwaysTrue(StringCollection collection) {
		return collection instanceof Collection<String>;
	}

	interface StringCollection extends Collection<String> {}
}
