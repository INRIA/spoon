/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import static spoon.reflect.path.CtRole.LABEL;

/**
 * This abstract code element represents all the statements, which can be part
 * of a block.
 *
 * @see spoon.reflect.code.CtBlock
 */
public interface CtStatement extends CtCodeElement {

	/**
	 * Inserts a statement after the current statement.
	 */
	<T extends CtStatement> T insertAfter(CtStatement statement) throws ParentNotInitializedException;

	/**
	 * Inserts a statement list before the current statement.
	 */
	<T extends CtStatement> T insertAfter(CtStatementList statements) throws ParentNotInitializedException;

	/**
	 * Inserts a statement given as parameter before the current statement
	 * (this).
	 */
	<T extends CtStatement> T insertBefore(CtStatement statement) throws ParentNotInitializedException;

	/**
	 * Inserts a statement list before the current statement.
	 */
	<T extends CtStatement> T insertBefore(CtStatementList statements) throws ParentNotInitializedException;

	/**
	 * Gets the label of this statement if defined.
	 *
	 * @return the label's name (null if undefined)
	 */
	@PropertyGetter(role = LABEL)
	String getLabel();

	/**
	 * Sets the label of this statement.
	 */
	@PropertySetter(role = LABEL)
	<T extends CtStatement> T setLabel(String label);

	@Override
	CtStatement clone();
}
