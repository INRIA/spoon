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
 * This code element defines an assignment.
 * 
 * @param <T>
 *            type of assigned expression
 * @param <A>
 *            type of expression to assign, it should extends <T>
 */
public interface CtAssignment<T, A extends T> extends CtStatement,
		CtExpression<T> {

	/**
	 * Returns the assigned expression (a variable, an array access...).
	 */
	CtExpression<T> getAssigned();

	/**
	 * Returns the assignment that is set to the assigned expression.
	 */
	CtExpression<A> getAssignment();

	/**
	 * Sets the assigned expression.
	 */
	void setAssigned(CtExpression<T> assigned);

	/**
	 * Sets the expression that is set to the assigned expression.
	 */
	void setAssignment(CtExpression<A> assignment);

}
