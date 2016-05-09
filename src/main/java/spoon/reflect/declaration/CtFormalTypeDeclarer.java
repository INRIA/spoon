package spoon.reflect.declaration;

import spoon.reflect.reference.CtTypeParameterReference;

import java.util.List;

/**
 * This abstract element defines a declaration that accepts formal type
 * parameters (aka generics).
 */
public interface CtFormalTypeDeclarer extends CtElement {
	/**
	 * Returns the formal type parameters of this generic element.
	 */
	List<CtTypeParameterReference> getFormalTypeParameters();

	/**
	 * Sets the type parameters of this generic element.
	 */
	<T extends CtFormalTypeDeclarer> T setFormalTypeParameters(List<CtTypeParameterReference> formalTypeParameters);

	/**
	 * Add a type parameter to this generic element.
	 *
	 * @param formalTypeParameter
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	<T extends CtFormalTypeDeclarer> T addFormalTypeParameter(CtTypeParameterReference formalTypeParameter);

	/**
	 * Removes a type parameters from this generic element.
	 *
	 * @param formalTypeParameter
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	boolean removeFormalTypeParameter(CtTypeParameterReference formalTypeParameter);
}
