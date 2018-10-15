package spoon.decompiler;

import spoon.reflect.declaration.CtType;

public interface TypeTransformer {

	/**
	 * User's implementation of transformation to apply on type.
	 * @param type type to be transformed
	 */
	void transform(CtType type);

	/**
	 * User defined filter to discard type that will not be transformed by the SpoonClassFileTransformer.
	 * @param type type considered for transformation
	 */
	default boolean accept(CtType type) {
		return true;
	}
}
