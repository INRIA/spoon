/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.support.reflect.code;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.annotations.MetamodelPropertyField;

public class CtBinaryOperatorImpl<T> extends CtExpressionImpl<T> implements CtBinaryOperator<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.OPERATOR_KIND)
	BinaryOperatorKind kind;

	@MetamodelPropertyField(role = CtRole.LEFT_OPERAND)
	CtExpression<?> leftHandOperand;

	@MetamodelPropertyField(role = CtRole.RIGHT_OPERAND)
	CtExpression<?> rightHandOperand;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtBinaryOperator(this);
	}

	@Override
	public CtExpression<?> getLeftHandOperand() {
		return leftHandOperand;
	}

	@Override
	public CtExpression<?> getRightHandOperand() {
		return rightHandOperand;
	}

	@Override
	public <C extends CtBinaryOperator<T>> C setLeftHandOperand(CtExpression<?> expression) {
		if (expression != null) {
			expression.setParent(this);
		}
		leftHandOperand = expression;
		return (C) this;
	}

	@Override
	public <C extends CtBinaryOperator<T>> C setRightHandOperand(CtExpression<?> expression) {
		if (expression != null) {
			expression.setParent(this);
		}
		rightHandOperand = expression;
		return (C) this;
	}

	@Override
	public <C extends CtBinaryOperator<T>> C setKind(BinaryOperatorKind kind) {
		this.kind = kind;
		return (C) this;
	}

	@Override
	public BinaryOperatorKind getKind() {
		return kind;
	}

	@Override
	public CtBinaryOperator<T> clone() {
		return (CtBinaryOperator<T>) super.clone();
	}
}
