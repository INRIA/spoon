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

import java.util.List;

import static spoon.reflect.path.CtRole.EXPRESSION;
import static spoon.reflect.path.CtRole.FOR_INIT;
import static spoon.reflect.path.CtRole.FOR_UPDATE;

/**
 * This code element defines a for loop.
 * Example:
 * <pre>
 *     // a for statement
 *     for(int i=0; i&lt;10; i++) {
 *     	System.out.println("foo");
 *     }
 * </pre>
 */
public interface CtFor extends CtLoop {

	/**
	 * Gets the end-loop test expression.
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression<Boolean> getExpression();

	/**
	 * Sets the end-loop test expression.
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtFor> T setExpression(CtExpression<Boolean> expression);

	/**
	 * Gets the <i>init</i> statements.
	 */
	@PropertyGetter(role = FOR_INIT)
	List<CtStatement> getForInit();

	/**
	 * Adds an <i>init</i> statement.
	 */
	@PropertySetter(role = FOR_INIT)
	<T extends CtFor> T addForInit(CtStatement statement);

	/**
	 * Sets the <i>init</i> statements.
	 */
	@PropertySetter(role = FOR_INIT)
	<T extends CtFor> T setForInit(List<CtStatement> forInit);

	/**
	 * Removes an <i>init</i> statement.
	 */
	@PropertySetter(role = FOR_INIT)
	boolean removeForInit(CtStatement statement);

	/**
	 * Gets the <i>update</i> statements.
	 */
	@PropertyGetter(role = FOR_UPDATE)
	List<CtStatement> getForUpdate();

	/**
	 * Adds an <i>update</i> statement.
	 */
	@PropertySetter(role = FOR_UPDATE)
	<T extends CtFor> T addForUpdate(CtStatement statement);

	/**
	 * Sets the <i>update</i> statements.
	 */
	@PropertySetter(role = FOR_UPDATE)
	<T extends CtFor> T setForUpdate(List<CtStatement> forUpdate);

	/**
	 * Removes an <i>update</i> statement.
	 */
	@PropertySetter(role = FOR_UPDATE)
	boolean removeForUpdate(CtStatement statement);

	@Override
	CtFor clone();
}
