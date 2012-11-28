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

package spoon.support.reflect.code;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.visitor.CtVisitor;

public class CtBinaryOperatorImpl<T> extends CtExpressionImpl<T> implements
		CtBinaryOperator<T> {
	private static final long serialVersionUID = 1L;

	CtExpression<?> leftHandOperand;

	CtExpression<?> rightHandOperand;

	public CtExpression<?> getLeftHandOperand() {
		return leftHandOperand;
	}

	public CtExpression<?> getRightHandOperand() {
		return rightHandOperand;
	}

	public void setLeftHandOperand(CtExpression<?> expression) {
		leftHandOperand = expression;
		leftHandOperand.setParent(this);

	}

	public void setRightHandOperand(CtExpression<?> expression) {
		rightHandOperand = expression;
		rightHandOperand.setParent(this);
	}

	BinaryOperatorKind kind;

	public void setKind(BinaryOperatorKind kind) {
		this.kind = kind;
	}

	public BinaryOperatorKind getKind() {
		return kind;
	}

	public void accept(CtVisitor visitor) {
		visitor.visitCtBinaryOperator(this);
	}

}
