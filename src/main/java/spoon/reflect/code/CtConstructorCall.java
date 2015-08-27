package spoon.reflect.code;

import spoon.reflect.reference.CtGenericElementReference;

/**
 * This code element represents a constructor call.
 *
 * @param <T>
 * 		created type
 */
public interface CtConstructorCall<T> extends CtTargetedExpression<T, CtExpression<?>>,
											  CtAbstractInvocation<T>,
											  CtStatement,
											  CtGenericElementReference {
}
