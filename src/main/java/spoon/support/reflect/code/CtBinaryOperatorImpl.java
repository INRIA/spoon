/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.visitor.CtVisitor;

import static spoon.reflect.path.CtRole.LEFT_OPERAND;
import static spoon.reflect.path.CtRole.OPERATOR_KIND;
import static spoon.reflect.path.CtRole.RIGHT_OPERAND;

public class CtBinaryOperatorImpl<T> extends CtExpressionImpl<T> implements CtBinaryOperator<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = OPERATOR_KIND)
	BinaryOperatorKind kind;

	@MetamodelPropertyField(role = LEFT_OPERAND)
	CtExpression<?> leftHandOperand;

	@MetamodelPropertyField(role = RIGHT_OPERAND)
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
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, LEFT_OPERAND, expression, this.leftHandOperand);
		leftHandOperand = expression;
		return (C) this;
	}

	@Override
	public <C extends CtBinaryOperator<T>> C setRightHandOperand(CtExpression<?> expression) {
		if (expression != null) {
			expression.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, RIGHT_OPERAND, expression, this.rightHandOperand);
		rightHandOperand = expression;
		return (C) this;
	}

	@Override
	public <C extends CtBinaryOperator<T>> C setKind(BinaryOperatorKind kind) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, OPERATOR_KIND, kind, this.kind);
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
