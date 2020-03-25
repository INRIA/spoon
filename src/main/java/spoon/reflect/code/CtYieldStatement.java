/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import static spoon.reflect.path.CtRole.EXPRESSION;

/**
 * This code element defines a <code>yield</code> statement.
 *
 * Example:
 * <pre>
 *     int x = 0;
 *     x = switch ("foo") {
 *         default -&gt; {
 * 					x=x+1;
 * 					yield x; //&lt;--- yield statement
 * 					}
 *     };
 * </pre>
 * A yield statement is implicit in the following example:
 * Example:
 * <pre>
 *     int x = 0;
 *     x = switch ("foo") {
 *         default -&gt; 4; //&lt;---  implicit yield statement
 *     };
 * </pre>
 * the example wouldn't be allowed without the brackets at the default case,
 * because java syntax defines case -&gt; [expression] or case -&gt; [blockStatement]
 * and yield is <b>not</b> a expression.
 */
public interface CtYieldStatement extends CtCFlowBreak  {
	/**
	 * Gets the expression of the yield statement.
	 * Example: case 1 -&gt; {yield 10};
	 * (This syntax is available as a feature since Java 14)
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression<?> getExpression();

	/**
	 * Gets the expression of the yield statement.
	 * Example: case 1 -&gt; {yield 10};
	 * (This syntax is available as a feature since Java 14)
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtYieldStatement> T setExpression(CtExpression<?> expression);

	@Override
	CtYieldStatement clone();
}
