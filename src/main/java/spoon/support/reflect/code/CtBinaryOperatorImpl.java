/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
