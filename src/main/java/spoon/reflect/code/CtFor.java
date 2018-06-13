/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
