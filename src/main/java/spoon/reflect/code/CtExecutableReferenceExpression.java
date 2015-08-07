package spoon.reflect.code;

import spoon.reflect.reference.CtExecutableReference;

/**
 * This abstract code element defines an expression which represents an executable reference.
 *
 * In Java, it is generally of the form: <code>Type::method</code>.
 *
 * @param <T>
 * 		Each executable references are typed by an interface with one method. This generic type
 * 		correspond to this concept.
 * @param <E>
 * 		Correspond of <code>Type</code> in <code>Type::method</code>.
 */
public interface CtExecutableReferenceExpression<T, E extends CtExpression<?>> extends CtTargetedExpression<T, E> {
	/**
	 * Gets the executable referenced by the expression.
	 */
	CtExecutableReference<T> getExecutable();

	/**
	 * Sets the executable will be referenced by the expression.
	 */
	<C extends CtExecutableReferenceExpression<T, E>> C setExecutable(CtExecutableReference<T> executable);
}
