/**
 * Copyright (C) 2006-2015 INRIA and contributors
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

import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtExpression;
import spoon.reflect.visitor.CtVisitor;

public class CtAssertImpl<T> extends CtStatementImpl implements CtAssert<T> {
	private static final long serialVersionUID = 1L;

	CtExpression<Boolean> asserted;

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
		asserted.setParent(this);
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
		this.value = value;
		return (A) this;
	}
}
