/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import static spoon.reflect.path.CtRole.EXPRESSION;

/**
 * This code element defines a break statement.
 * Example:
 * <pre>
 *     for(int i=0; i&lt;10; i++) {
 *         if (i&gt;3) {
 *				break; // &lt;-- break statement
 *         }
 *     }
 * </pre>
 */
public interface CtBreak extends CtLabelledFlowBreak {

	/**
	 * Gets the expression of the implicit brake with arrow syntax.
	 * Example: case 1 -> x = 10; (implicit brake with expression x = 10);
	 * (This syntax is available as a preview feature since Java 12)
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression<?> getExpression();

	/**
	 * Sets the expression of the implicit brake with arrow syntax.
	 * Example: case 1 -> x = 10; (implicit brake with expression x = 10);
	 * (This syntax is available as a preview feature since Java 12)
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtBreak> T setExpression(CtExpression<?> expression);

	@Override
	CtBreak clone();
}
