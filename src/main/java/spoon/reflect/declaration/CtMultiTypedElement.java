package spoon.reflect.declaration;

import spoon.reflect.reference.CtTypeReference;

import java.util.List;

/**
 * Defined an element with several types.
 */
public interface CtMultiTypedElement extends CtElement {
	/**
	 * Adds a type for the element.
	 */
	<T extends CtMultiTypedElement> T addMultiType(CtTypeReference<?> ref);

	/**
	 * Removes a type for the element.
	 */
	boolean removeMultiType(CtTypeReference<?> ref);

	/**
	 * Gets all types of the element.
	 */
	List<CtTypeReference<?>> getMultiTypes();
}
