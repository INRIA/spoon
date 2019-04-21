/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import java.util.List;

import static spoon.reflect.path.CtRole.CASE;
import static spoon.reflect.path.CtRole.EXPRESSION;

/**
 * This code element defines a switch statement.
 *
 * Example: <pre>
 * int x = 0;
 * switch(x) { // &lt;-- switch statement
 *     case 1:
 *       System.out.println("foo");
 * }</pre>

 * @param <S>
 * 		the type of the selector expression (it would be better to be able
 * 		to define an upper bound, but it is not possible because of Java's
 * 		type hierarchy, especially since the enums that make things even
 * 		worse!)
 */
public interface CtSwitch<S> extends CtStatement {
	/**
	 * Gets the selector. The type of the Expression must be <code>char</code>,
	 * <code>byte</code>, <code>short</code>, <code>int</code>,
	 * <code>Character</code>, <code>Byte</code>, <code>Short</code>,
	 * <code>Integer</code>, or an <code>enum</code> type
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression<S> getSelector();

	/**
	 * Sets the selector. The type of the Expression must be <code>char</code>,
	 * <code>byte</code>, <code>short</code>, <code>int</code>,
	 * <code>Character</code>, <code>Byte</code>, <code>Short</code>,
	 * <code>Integer</code>, or an <code>enum</code> type
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtSwitch<S>> T setSelector(CtExpression<S> selector);

	/**
	 * Gets the list of cases defined for this switch.
	 */
	@PropertyGetter(role = CASE)
	List<CtCase<? super S>> getCases();

	/**
	 * Sets the list of cases defined for this switch.
	 */
	@PropertySetter(role = CASE)
	<T extends CtSwitch<S>> T setCases(List<CtCase<? super S>> cases);

	/**
	 * Adds a case;
	 */
	@PropertySetter(role = CASE)
	<T extends CtSwitch<S>> T addCase(CtCase<? super S> c);

	/**
	 * Removes a case;
	 */
	@PropertySetter(role = CASE)
	boolean removeCase(CtCase<? super S> c);

	@Override
	CtSwitch<S> clone();
}
