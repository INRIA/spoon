/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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
 * This code element defines a <code>for</code> loop.
 */
public interface CtFor extends CtLoop {

	/**
	 * Gets the end-loop test expression.
	 *
	 * @return the expression of the condition
	 */
	CtExpression<Boolean> getExpression();

	/**
	 * Gets the <i>init</i> statements.
	 *
	 * @return the initialization statement
	 */
	List<CtStatement> getForInit();

	/**
	 * Adds an <i>init</i> statement.
	 *
	 * @param statement the statement to add
	 *
	 * @return true if the statement has been added
	 */
	boolean addForInit(CtStatement statement);

	/**
	 * Removes an <i>init</i> statement.
	 *
	 * @param statement the statement to remove
	 *
	 * @return true if the statement has een removed
	 */
	boolean removeForInit(CtStatement statement);

	/**
	 * Gets the <i>update</i> statements.
	 *
	 * @return the List of update statements
	 */
	List<CtStatement> getForUpdate();

	/**
	 * Sets the end-loop test expression.
	 *
	 * @param expression the expression to set for the condition
	 */
	void setExpression(CtExpression<Boolean> expression);

	/**
	 * Sets the <i>init</i> statements.
	 *
	 * @param forInit the List of initialization statements to set
	 */
	void setForInit(List<CtStatement> forInit);

	/**
	 * Sets the <i>update</i> statements.
	 *
	 * @param forUpdate the List if update statements to set
	 */
	void setForUpdate(List<CtStatement> forUpdate);

	/**
	 * Adds an <i>update</i> statement.
	 *
	 * @param statement the statement to add
	 *
	 * @return true if the update statement has been added
	 */
	boolean addForUpdate(CtStatement statement);

	/**
	 * Removes an <i>update</i> statement.
	 *
	 * @param statement the statement to remove
	 *
	 * @return true of the statement has been removed
	 */
	boolean removeForUpdate(CtStatement statement);

}
