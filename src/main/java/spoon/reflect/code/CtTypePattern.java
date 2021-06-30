package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.UnsettableProperty;

import java.util.List;

import static spoon.reflect.path.CtRole.VARIABLE;

/**
 * This code element defines a type pattern, introduced in Java 16
 * by <a href=https://openjdk.java.net/jeps/394>JEP 394</a>.
 * <p>
 * Example:
 * <pre>
 *     // String s is the type pattern, declaring a local variable
 *     if (obj instanceof String s) {
 *         return s.length() > 2;
 *     }
 * </pre>
 *
 * @param <T> the type of the variable.
 */
public interface CtTypePattern<T> extends CtPattern {

    /**
     * Returns the local variable declared by this type pattern.
     */
    @PropertyGetter(role = VARIABLE)
    CtLocalVariable<?> getVariable();

	/**
	 * Sets the local variable for this type pattern.
	 */
    @PropertySetter(role = VARIABLE)
    <C extends CtTypePattern<?>> C setVariable(CtLocalVariable<?> variable);

    @Override
    CtTypePattern<T> clone();

	@Override
	@UnsettableProperty
	List<CtTypeReference<?>> getTypeCasts();

	@Override
	@UnsettableProperty
	<C extends CtExpression<Void>> C setTypeCasts(List<CtTypeReference<?>> types);

	@Override
	@UnsettableProperty
	<C extends CtExpression<Void>> C addTypeCast(CtTypeReference<?> type);
}
