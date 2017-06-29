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

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;

import static spoon.reflect.path.CtRole.BODY;
import static spoon.reflect.path.CtRole.EXPRESSION;

public class CtSynchronizedImpl extends CtStatementImpl implements CtSynchronized {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.BODY)
	CtBlock<?> block;

	@MetamodelPropertyField(role = CtRole.EXPRESSION)
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
