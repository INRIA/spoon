/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.visitor.CtVisitor;

import static spoon.reflect.path.CtRole.EXPRESSION;
import static spoon.reflect.path.CtRole.LABEL;
import static spoon.reflect.path.CtRole.OPERATOR_KIND;

public class CtUnaryOperatorImpl<T> extends CtExpressionImpl<T> implements CtUnaryOperator<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = OPERATOR_KIND)
	UnaryOperatorKind kind;

	@MetamodelPropertyField(role = LABEL)
	String label;

	@MetamodelPropertyField(role = EXPRESSION)
	CtExpression<T> operand;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtUnaryOperator(this);
	}

	@Override
	public CtExpression<T> getOperand() {
		return operand;
	}

	@Override
	public UnaryOperatorKind getKind() {
		return kind;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public <C extends CtStatement> C insertAfter(CtStatement statement) {
		CtStatementImpl.insertAfter(this, statement);
		return (C) this;
	}

	@Override
	public <C extends CtStatement> C insertBefore(CtStatement statement) {
		CtStatementImpl.insertBefore(this, statement);
		return (C) this;
	}

	@Override
	public <C extends CtStatement> C insertAfter(CtStatementList statements) {
		CtStatementImpl.insertAfter(this, statements);
		return (C) this;
	}

	@Override
	public <C extends CtStatement> C insertBefore(CtStatementList statements) {
		CtStatementImpl.insertBefore(this, statements);
		return (C) this;
	}

	@Override
	public <C extends CtUnaryOperator> C setOperand(CtExpression<T> expression) {
		if (expression != null) {
			expression.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, EXPRESSION, expression, this.operand);
		this.operand = expression;
		return (C) this;
	}

	@Override
	public <C extends CtUnaryOperator> C setKind(UnaryOperatorKind kind) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, OPERATOR_KIND, kind, this.kind);
		this.kind = kind;
		return (C) this;
	}

	@Override
	public <C extends CtStatement> C setLabel(String label) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, LABEL, label, this.label);
		this.label = label;
		return (C) this;
	}

	@Override
	public CtUnaryOperator<T> clone() {
		return (CtUnaryOperator<T>) super.clone();
	}
}
