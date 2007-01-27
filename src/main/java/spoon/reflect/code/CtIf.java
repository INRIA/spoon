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

import spoon.template.TemplateParameter;

/**
 * This code element represents an <code>if</code> statement.
 */
public interface CtIf extends CtStatement, TemplateParameter<Void> {

	/**
	 * Gets the boolean expression that represents the <code>if</code>'s
	 * condition.
	 */
	CtExpression<Boolean> getCondition();

	/**
	 * Gets the statement executed when the condition is false.
	 */
	CtStatement getElseStatement();

	/**
	 * Gets the statement executed when the condition is true.
	 */
	CtStatement getThenStatement();

	/**
	 * Sets the boolean expression that represents the <code>if</code>'s
	 * condition.
	 */
	void setCondition(CtExpression<Boolean> expression);

	/**
	 * Sets the statement executed when the condition is false.
	 */
	void setElseStatement(CtStatement elseStatement);

	/**
	 * Sets the statement executed when the condition is true.
	 */
	void setThenStatement(CtStatement thenStatement);

}
