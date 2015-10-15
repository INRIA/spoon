package spoon.reflect.factory;

import spoon.reflect.internal.CtCircularTypeReference;

/**
 * This interface defines the creation methods for internal nodes of the
 * meta-model. These nodes are available in the AST provided by Spoon
 * but their creation should be used only in internal of Spoon.
 */
public interface InternalFactory {
	/**
	 * Creates a circular type reference.
	 */
	<T> CtCircularTypeReference createCircularTypeReference();
}
