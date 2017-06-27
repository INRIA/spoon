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
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtExpression;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;

import static spoon.reflect.path.CtRole.CONDITION;
import static spoon.reflect.path.CtRole.EXPRESSION;

public class CtAssertImpl<T> extends CtStatementImpl implements CtAssert<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.CONDITION)
	CtExpression<Boolean> asserted;

	@MetamodelPropertyField(role = CtRole.EXPRESSION)
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
