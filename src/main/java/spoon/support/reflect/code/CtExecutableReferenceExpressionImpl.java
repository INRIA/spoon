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

import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.CtVisitor;

public class CtExecutableReferenceExpressionImpl<T, E extends CtExpression<?>> extends CtTargetedExpressionImpl<T, E> implements CtExecutableReferenceExpression<T, E> {
	CtExecutableReference<T> executable;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtExecutableReferenceExpression(this);
	}

	@Override
	public CtExecutableReference<T> getExecutable() {
		return executable;
	}

	@Override
	public <C extends CtExecutableReferenceExpression<T, E>> C setExecutable(CtExecutableReference<T> executable) {
		if (executable != null) {
			executable.setParent(this);
		}
		this.executable = executable;
		return (C) this;
	}

	@Override
	public CtExecutableReferenceExpression<T, E> clone() {
		return (CtExecutableReferenceExpression<T, E>) super.clone();
	}
}
