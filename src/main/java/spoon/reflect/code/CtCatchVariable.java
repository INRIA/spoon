package spoon.reflect.code;

import spoon.reflect.declaration.CtMultiTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtCatchVariableReference;

/**
 * This code element defines an exception variable in a catch.
 *
 * @param <T>
 * 		type of the variable
 */
public interface CtCatchVariable<T> extends CtVariable<T>, CtMultiTypedElement, CtCodeElement {

	/*
	 * (non-Javadoc)
	 *
	 * @see spoon.reflect.declaration.CtNamedElement#getReference()
	 */
	CtCatchVariableReference<T> getReference();
}
