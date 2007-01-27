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
 * This interface defines a binary operator.
 * 
 * @param <T>
 *            Type of this expression
 */
public interface CtBinaryOperator<T> extends CtExpression<T> {

	/**
	 * Returns the left-hand operand.
	 */
	CtExpression<?> getLeftHandOperand();

	/**
	 * Returns the right-hand operand.
	 */
	CtExpression<?> getRightHandOperand();

	/**
	 * Sets the left-hand operand.
	 */
	void setLeftHandOperand(CtExpression<?> expression);

	/**
	 * Sets the right-hand operand.
	 */
	void setRightHandOperand(CtExpression<?> expression);

	/**
	 * Sets the kind of this binary operator.
	 */
	void setKind(BinaryOperatorKind kind);

	/**
	 * Gets the kind of this binary operator.
	 */
	BinaryOperatorKind getKind();

}
