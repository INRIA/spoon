package spoon.reflect.internal;

import spoon.reflect.reference.CtTypeReference;

/**
 * This interface defines a reference to a {@link spoon.reflect.declaration.CtType} or sub-type
 * but when this type is implicit like given in the diamond operator or parameter of a lambda.
 *
 * <pre>
 * {@code
 *     // The type in the diamond operator of ArrayList is a CtImplicitTypeReference with a String.
 *     List<String> list = new ArrayList<>();
 *
 *     (e) -> {}
 * }
 * </pre>
 *
 * @param <T>
 * 		Implicit type.
 */
public interface CtImplicitTypeReference<T> extends CtTypeReference<T> {
}
