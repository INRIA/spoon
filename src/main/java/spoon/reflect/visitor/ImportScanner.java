package spoon.reflect.visitor;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.util.Collection;

public interface ImportScanner {
	/**
	 * Computes import of a {@link spoon.reflect.declaration.CtType}
	 * (represent a class).
	 *
	 * @return Imports computes by Spoon, not original imports.
	 */
	Collection<CtTypeReference<?>> computeImports(CtType<?> simpleType);

	/**
	 * Computes imports for all elements.
	 */
	void computeImports(CtElement element);

	/**
	 * Checks if the type is already imported.
	 */
	boolean isImported(CtTypeReference<?> ref);
}
