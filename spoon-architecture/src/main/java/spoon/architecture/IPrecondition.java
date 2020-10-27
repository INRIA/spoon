package spoon.architecture;

import java.util.Collection;
import java.util.function.Function;

/**
 * This defines a precondition for a {@link ArchitectureTest} and is a filter for meta model elements.
 * It's a function from a meta model to a collection of meta model elements.
 * @param T  the type of meta model elements
 * @param M  the meta model type
 */
public interface IPrecondition<T, M> extends Function<M, Collection<T>> {
	// Marker interface for documentation
}
