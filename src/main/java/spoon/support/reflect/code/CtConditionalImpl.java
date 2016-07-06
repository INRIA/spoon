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

import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtExpression;
import spoon.reflect.visitor.CtVisitor;

public class CtConditionalImpl<T> extends CtExpressionImpl<T> implements CtConditional<T> {
	private static final long serialVersionUID = 1L;

	CtExpression<T> elseExpression;

	CtExpression<Boolean> condition;

	CtExpression<T> thenExpression;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtConditional(this);
	}

	@Override
	public CtExpression<T> getElseExpression() {
		return elseExpression;
	}

	@Override
	public CtExpression<Boolean> getCondition() {
		return condition;
	}

	@Override
	public CtExpression<T> getThenExpression() {
		return thenExpression;
	}

	@Override
	public <C extends CtConditional<T>> C setElseExpression(CtExpression<T> elseExpression) {
		if (elseExpression != null) {
			elseExpression.setParent(this);
		}
		this.elseExpression = elseExpression;
		return (C) this;
	}

	@Override
	public <C extends CtConditional<T>> C setCondition(CtExpression<Boolean> condition) {
		if (condition != null) {
			condition.setParent(this);
		}
		this.condition = condition;
		return (C) this;
	}

	@Override
	public <C extends CtConditional<T>> C setThenExpression(CtExpression<T> thenExpression) {
		if (thenExpression != null) {
			thenExpression.setParent(this);
		}
		this.thenExpression = thenExpression;
		return (C) this;
	}

	@Override
	public CtConditional<T> clone() {
		return (CtConditional<T>) super.clone();
	}
}
