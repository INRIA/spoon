/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.visitor.CtVisitor;

public class CtSynchronizedImpl extends CtStatementImpl implements CtSynchronized {
	private static final long serialVersionUID = 1L;

	CtBlock<?> block;

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
		this.block = block;
		return (T) this;
	}

	@Override
	public <T extends CtSynchronized> T setExpression(CtExpression<?> expression) {
		if (expression != null) {
			expression.setParent(this);
		}
		this.expression = expression;
		return (T) this;
	}

	@Override
	public CtSynchronized clone() {
		return (CtSynchronized) super.clone();
	}
}
