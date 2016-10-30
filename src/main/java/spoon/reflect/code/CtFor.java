/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

import java.util.List;

/**
 * This code element defines a for loop.
 * Example:
 * <pre>
 *     // a for statement
 *     for(int i=0; i<10; i++) {
 *     	System.out.println("foo");
 *     }
 * </pre>
 */
public interface CtFor extends CtLoop {

	/**
	 * Gets the end-loop test expression.
	 */
	CtExpression<Boolean> getExpression();

	/**
	 * Sets the end-loop test expression.
	 */
	<T extends CtFor> T setExpression(CtExpression<Boolean> expression);

	/**
	 * Gets the <i>init</i> statements.
	 */
	List<CtStatement> getForInit();

	/**
	 * Adds an <i>init</i> statement.
	 */
	<T extends CtFor> T addForInit(CtStatement statement);

	/**
	 * Sets the <i>init</i> statements.
	 */
	<T extends CtFor> T setForInit(List<CtStatement> forInit);

	/**
	 * Removes an <i>init</i> statement.
	 */
	boolean removeForInit(CtStatement statement);

	/**
	 * Gets the <i>update</i> statements.
	 */
	List<CtStatement> getForUpdate();

	/**
	 * Adds an <i>update</i> statement.
	 */
	<T extends CtFor> T addForUpdate(CtStatement statement);

	/**
	 * Sets the <i>update</i> statements.
	 */
	<T extends CtFor> T setForUpdate(List<CtStatement> forUpdate);

	/**
	 * Removes an <i>update</i> statement.
	 */
	boolean removeForUpdate(CtStatement statement);

	@Override
	CtFor clone();
}
