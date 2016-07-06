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

import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.CtVisitor;

public class CtReturnImpl<R> extends CtStatementImpl implements CtReturn<R> {
	private static final long serialVersionUID = 1L;

	CtExpression<R> returnedExpression;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtReturn(this);
	}

	@Override
	public CtExpression<R> getReturnedExpression() {
		return returnedExpression;
	}

	@Override
	public <T extends CtReturn<R>> T setReturnedExpression(CtExpression<R> expression) {
		if (expression != null) {
			expression.setParent(this);
		}
		this.returnedExpression = expression;
		return (T) this;
	}

	@Override
	public CtReturn<R> clone() {
		return (CtReturn<R>) super.clone();
	}

	@Override
	public Void S() {
		return null;
	}

	public CtCodeElement getSubstitution(CtType<?> targetType) {
		return clone();
	}
}
