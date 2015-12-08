/**
 * Copyright (C) 2006-2015 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.reflect.code;

import spoon.reflect.declaration.ParentNotInitializedException;

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
	String getLabel();

	/**
	 * Sets the label of this statement.
	 */
	<T extends CtStatement> T setLabel(String label);

	/**
	 * Replaces this element by another one.
	 */
	void replace(CtStatement element);
}
