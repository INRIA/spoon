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
import spoon.template.TemplateParameter;

import static spoon.reflect.path.CtRole.EXPRESSION;

/**
 * This code element defines a <code>throw</code> statement.
 *
 * Example:
 * <pre>
 *     throw new RuntimeException("oops")
 * </pre>
 */
public interface CtThrow extends CtCFlowBreak, TemplateParameter<Void> {

	/**
	 * Returns the thrown expression (must be a throwable).
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression<? extends Throwable> getThrownExpression();

	/**
	 * Sets the thrown expression (must be a throwable).
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtThrow> T setThrownExpression(CtExpression<? extends Throwable> thrownExpression);

	@Override
	CtThrow clone();
}
