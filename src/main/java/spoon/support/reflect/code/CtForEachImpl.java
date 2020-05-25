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
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.visitor.CtVisitor;

import static spoon.reflect.path.CtRole.EXPRESSION;
import static spoon.reflect.path.CtRole.FOREACH_VARIABLE;

public class CtForEachImpl extends CtLoopImpl implements CtForEach {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = EXPRESSION)
	CtExpression<?> expression;

	@MetamodelPropertyField(role = FOREACH_VARIABLE)
	CtLocalVariable<?> variable;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtForEach(this);
	}

	@Override
	public CtExpression<?> getExpression() {
		return expression;
	}

	@Override
	public CtLocalVariable<?> getVariable() {
		return variable;
	}

	@Override
	public <T extends CtForEach> T setExpression(CtExpression<?> expression) {
		if (expression != null) {
			expression.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, EXPRESSION, expression, this.expression);
		this.expression = expression;
		return (T) this;
	}

	@Override
	public <T extends CtForEach> T setVariable(CtLocalVariable<?> variable) {
		if (variable != null) {
			variable.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, FOREACH_VARIABLE, variable, this.variable);
		this.variable = variable;
		return (T) this;
	}

	@Override
	public CtForEach clone() {
		return (CtForEach) super.clone();
	}
}
