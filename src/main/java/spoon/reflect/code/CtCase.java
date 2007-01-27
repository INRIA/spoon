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
 * This code element defines a <code>case</code> within a <code>switch</code>.
 * 
 * @param <S>
 *            This type must be assignable from the switch type
 * @see spoon.reflect.code.CtSwitch
 */
public interface CtCase<S> extends CtStatement {
	/**
	 * Gets the case expression.
	 */
	CtExpression<S> getCaseExpression();

	/**
	 * Sets the case expression.
	 */
	void setCaseExpression(CtExpression<S> caseExpression);

	/**
	 * Gets the list of statements that defines the case body.
	 */
	List<CtStatement> getStatements();

	/**
	 * Sets the list of statements that defines the case body.
	 */
	void setStatements(List<CtStatement> statements);
}
