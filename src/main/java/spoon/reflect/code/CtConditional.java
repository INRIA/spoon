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

/**
 * This code element defines conditional expressions using the ?.
 */
public interface CtConditional<T> extends CtExpression<T> {

	/**
	 * Gets the "false" expression.
	 *
	 * @return the expression of the false section
	 */
	CtExpression<T> getElseExpression();

	/**
	 * Gets the "true" expression.
	 *
	 * @return the expression of the true section
	 */
	CtExpression<T> getThenExpression();

	/**
	 * Gets the condition expression.
	 *
	 * @return the condition expression
	 */
	CtExpression<Boolean> getCondition();

	/**
	 * Sets the "false" expression.
	 *
	 * @param elseExpression the expression for the else section
	 */
	void setElseExpression(CtExpression<T> elseExpression);

	/**
	 * Sets the "true" expression.
	 *
	 * @param thenExpression the expression for the true section
	 */
	void setThenExpression(CtExpression<T> thenExpression);

	/**
	 * Sets the condition expression.
	 *
	 * @param condition the condition expression
	 */
	void setCondition(CtExpression<Boolean> condition);

}
