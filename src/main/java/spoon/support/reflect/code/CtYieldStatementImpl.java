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
import spoon.reflect.code.CtYieldStatement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;

import static spoon.reflect.path.CtRole.EXPRESSION;


/**
 * CtYieldStatementImpl
 */
public class CtYieldStatementImpl extends CtStatementImpl implements CtYieldStatement {

	/**
	 * make the serialization gods happy
	 */
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.EXPRESSION)
	CtExpression<?> expression;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtYieldStatement(this);
	}

	@Override
	public CtExpression<?> getExpression() {
		return expression;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends CtYieldStatement> T setExpression(CtExpression<?> expression) {
		if (expression != null) {
			expression.setParent(this);
		}
		getFactory().getEnvironment()
				.getModelChangeListener()
				.onObjectUpdate(this, EXPRESSION, expression, this.expression);
		this.expression = expression;
		return (T) this;
	}

	@Override
	public CtYieldStatement clone() {
		return CtYieldStatement.class.cast(super.clone());
	}
}
