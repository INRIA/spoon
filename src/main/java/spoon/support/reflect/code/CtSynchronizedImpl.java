/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.visitor.CtVisitor;

import static spoon.reflect.path.CtRole.BODY;
import static spoon.reflect.path.CtRole.EXPRESSION;

public class CtSynchronizedImpl extends CtStatementImpl implements CtSynchronized {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = BODY)
	CtBlock<?> block;

	@MetamodelPropertyField(role = EXPRESSION)
	CtExpression<?> expression;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtSynchronized(this);
	}

	@Override
	public CtBlock<?> getBlock() {
		return block;
	}

	@Override
	public CtExpression<?> getExpression() {
		return expression;
	}

	@Override
	public <T extends CtSynchronized> T setBlock(CtBlock<?> block) {
		if (block != null) {
			block.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, BODY, block, this.block);
		this.block = block;
		return (T) this;
	}

	@Override
	public <T extends CtSynchronized> T setExpression(CtExpression<?> expression) {
		if (expression != null) {
			expression.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, EXPRESSION, expression, this.expression);
		this.expression = expression;
		return (T) this;
	}

	@Override
	public CtSynchronized clone() {
		return (CtSynchronized) super.clone();
	}
}
