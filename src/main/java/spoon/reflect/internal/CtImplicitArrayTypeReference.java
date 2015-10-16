package spoon.reflect.internal;

import spoon.reflect.reference.CtArrayTypeReference;

/**
 * This interface defines a reference to an array but when this array is implicit
 * like given in the diamond operator or parameter of a lambda.
 *
 * {@code
 * final AtomicReference<AtomicLong[]> atomicReference = new AtomicReference<>(EMPTY);
 * }
 *
 * @param <T>
 * 		Implicit type.
 */
public interface CtImplicitArrayTypeReference<T> extends CtArrayTypeReference<T> {
}
