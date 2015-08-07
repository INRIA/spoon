package spoon.reflect.code;

import spoon.reflect.declaration.CtField;

/** Represents the right hand side of an assignment 
 * 
 * See {@link CtAssignment}, {@link CtLocalVariable}, {@link CtField}
 */
public interface CtRHSReceiver<A> {
	/**
	 * Returns the right-hand side of the "=" operator.
	 */
	CtExpression<A> getAssignment();

	/**
	 * Sets the right-hand side expression (RHS) of the "=" operator.
	 */
	<T extends CtRHSReceiver<A>> T setAssignment(CtExpression<A> assignment);
}
