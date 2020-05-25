/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtExpression;
import spoon.reflect.visitor.CtVisitor;

import static spoon.reflect.path.CtRole.CONDITION;
import static spoon.reflect.path.CtRole.EXPRESSION;

public class CtAssertImpl<T> extends CtStatementImpl implements CtAssert<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CONDITION)
	CtExpression<Boolean> asserted;

	@MetamodelPropertyField(role = EXPRESSION)
	CtExpression<T> value;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtAssert(this);
	}

	@Override
	public CtExpression<Boolean> getAssertExpression() {
		return asserted;
	}

	@Override
	public <A extends CtAssert<T>> A setAssertExpression(CtExpression<Boolean> asserted) {
		if (asserted != null) {
			asserted.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CONDITION, asserted, this.asserted);
		this.asserted = asserted;
		return (A) this;
	}

	@Override
	public CtExpression<T> getExpression() {
		return value;
	}

	@Override
	public <A extends CtAssert<T>> A setExpression(CtExpression<T> value) {
		if (value != null) {
			value.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, EXPRESSION, value, this.value);
		this.value = value;
		return (A) this;
	}

	@Override
	public CtAssert<T> clone() {
		return (CtAssert<T>) super.clone();
	}
}
