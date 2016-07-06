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

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.visitor.CtVisitor;

public class CtForEachImpl extends CtLoopImpl implements CtForEach {
	private static final long serialVersionUID = 1L;

	CtExpression<?> expression;

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
		this.expression = expression;
		return (T) this;
	}

	@Override
	public <T extends CtForEach> T setVariable(CtLocalVariable<?> variable) {
		if (variable != null) {
			variable.setParent(this);
		}
		this.variable = variable;
		return (T) this;
	}

	@Override
	public CtForEach clone() {
		return (CtForEach) super.clone();
	}
}
