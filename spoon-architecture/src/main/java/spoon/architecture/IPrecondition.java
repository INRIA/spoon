package spoon.architecture;

import java.util.Collection;
import java.util.function.Function;

public interface IPrecondition<T, M> extends Function<M, Collection<T>> {
	// Marker interface
}
