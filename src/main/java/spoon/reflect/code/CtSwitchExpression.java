/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

/**
 * This code element defines a switch expression.
 *
 * Example: <pre>
 * int i = 0;
 * int x = switch(i) { // &lt;-- switch expression
 *     case 1 -&gt; 10;
 *     case 2 -&gt; 20;
 *     default -&gt; 30;
 * };</pre>

 * @param <T>
 * 		the type of the switch expression
 * @param <S>
 * 		the type of the selector expression (it would be better to be able
 * 		to define an upper bound, but it is not possible because of Java's
 * 		type hierarchy, especially since the enums that make things even
 * 		worse!)
 */
public interface CtSwitchExpression<T, S>  extends CtExpression<T>, CtAbstractSwitch<S> {

	@Override
	CtSwitchExpression<T, S> clone();
}
