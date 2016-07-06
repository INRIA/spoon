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
import spoon.reflect.code.CtTargetedExpression;

public abstract class CtTargetedExpressionImpl<E, T extends CtExpression<?>> extends CtExpressionImpl<E> implements CtTargetedExpression<E, T> {
	private static final long serialVersionUID = 1L;

	T target;

	@Override
	public T getTarget() {
		return target;
	}

	@Override
	public <C extends CtTargetedExpression<E, T>> C setTarget(T target) {
		if (target != null) {
			target.setParent(this);
		}
		this.target = target;
		return (C) this;
	}

	@Override
	public CtTargetedExpression<E, T> clone() {
		return (CtTargetedExpression<E, T>) super.clone();
	}
}
