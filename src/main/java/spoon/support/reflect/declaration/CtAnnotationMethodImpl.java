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
package spoon.support.reflect.declaration;

import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.visitor.CtVisitor;

/**
 * The implementation for {@link spoon.reflect.declaration.CtAnnotationMethod}.
 */
public class CtAnnotationMethodImpl<T> extends CtMethodImpl<T> implements CtAnnotationMethod<T> {
	CtExpression<T> defaultExpression;

	@Override
	public void accept(CtVisitor v) {
		v.visitCtAnnotationMethod(this);
	}

	@Override
	public CtExpression<T> getDefaultExpression() {
		return defaultExpression;
	}

	@Override
	public <C extends CtAnnotationMethod<T>> C setDefaultExpression(CtExpression<T> assignedExpression) {
		if (assignedExpression != null) {
			assignedExpression.setParent(this);
		}
		this.defaultExpression = assignedExpression;
		return (C) this;
	}

	@Override
	public CtAnnotationMethod<T> clone() {
		return (CtAnnotationMethod<T>) super.clone();
	}
}
