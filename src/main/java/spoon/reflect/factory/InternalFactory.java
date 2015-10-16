package spoon.reflect.factory;

import spoon.reflect.internal.CtCircularTypeReference;
import spoon.reflect.internal.CtImplicitArrayTypeReference;
import spoon.reflect.internal.CtImplicitTypeReference;

/**
 * This interface defines the creation methods for internal nodes of the
 * meta-model. These nodes are available in the AST provided by Spoon
 * but their creation should be used only in internal of Spoon.
 */
public interface InternalFactory {
	/**
	 * Creates a circular type reference.
	 */
	CtCircularTypeReference createCircularTypeReference();

	/**
	 * Creates a inference type reference.
	 */
	<T> CtImplicitTypeReference<T> createImplicitTypeReference();

	/**
	 * Creates an implicit array type reference.
	 */
	<T> CtImplicitArrayTypeReference<T> createImplicitArrayTypeReference();
}
