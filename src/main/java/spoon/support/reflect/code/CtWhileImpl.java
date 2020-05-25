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
import spoon.reflect.code.CtWhile;
import spoon.reflect.visitor.CtVisitor;

import static spoon.reflect.path.CtRole.EXPRESSION;

public class CtWhileImpl extends CtLoopImpl implements CtWhile {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = EXPRESSION)
	CtExpression<Boolean> expression;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtWhile(this);
	}

	@Override
	public CtExpression<Boolean> getLoopingExpression() {
		return expression;
	}

	@Override
	public <T extends CtWhile> T setLoopingExpression(CtExpression<Boolean> expression) {
		if (expression != null) {
			expression.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, EXPRESSION, expression, this.expression);
		this.expression = expression;
		return (T) this;
	}

	@Override
	public CtWhile clone() {
		return (CtWhile) super.clone();
	}
}
