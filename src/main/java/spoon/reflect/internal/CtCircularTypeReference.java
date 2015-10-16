package spoon.reflect.internal;

import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * When we build a {@link CtTypeReference}, we can have a circular when
 * we got this kind of generic type: {@code <T extends Comparable<? super T>>}.
 * In this case, where we are at the last T, we come back at the first
 * one and we are in a circular.
 *
 * Now, the last T is a CtCircularTypeReference and we stop the circular
 * when we build the generic or when we scan an AST given.
 */
public interface CtCircularTypeReference extends CtTypeParameterReference {
}
