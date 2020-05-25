/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtExpression;

import static spoon.reflect.path.CtRole.EXPRESSION;

public abstract class CtArrayAccessImpl<T, V extends CtExpression<?>> extends CtTargetedExpressionImpl<T, V> implements CtArrayAccess<T, V> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = EXPRESSION)
	private CtExpression<Integer> expression;

	@Override
	public CtExpression<Integer> getIndexExpression() {
		return expression;
	}

	@Override
	public <C extends CtArrayAccess<T, V>> C setIndexExpression(CtExpression<Integer> expression) {
		if (expression != null) {
			expression.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, EXPRESSION, expression, this.expression);
		this.expression = expression;
		return (C) this;
	}

	@Override
	public CtArrayAccess<T, V> clone() {
		return (CtArrayAccess<T, V>) super.clone();
	}
}
