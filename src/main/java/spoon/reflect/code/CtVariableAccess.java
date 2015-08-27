package spoon.reflect.code;

import spoon.reflect.reference.CtVariableReference;

/**
 * This code element defines an access to a variable (read and write).
 *
 * @param <T>
 * 		type of the variable
 */
public interface CtVariableAccess<T> extends CtExpression<T> {
	/**
	 * Gets the reference to the accessed variable.
	 */
	CtVariableReference<T> getVariable();

	/**
	 * Sets the reference to the accessed variable.
	 */
	<C extends CtVariableAccess<T>> C setVariable(CtVariableReference<T> variable);
}
