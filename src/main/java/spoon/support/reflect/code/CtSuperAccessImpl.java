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

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.visitor.CtVisitor;

public class CtSuperAccessImpl<T> extends CtVariableReadImpl<T> implements CtSuperAccess<T> {

	private static final long serialVersionUID = 1L;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtSuperAccess(this);
	}

	CtExpression<?> target;

	@Override
	public CtExpression<?> getTarget() {
		return target;
	}

	@Override
	public <C extends CtTargetedExpression<T, CtExpression<?>>> C setTarget(CtExpression<?> target) {
		if (target != null) {
			target.setParent(this);
		}
		this.target = target;
		return null;
	}
}
