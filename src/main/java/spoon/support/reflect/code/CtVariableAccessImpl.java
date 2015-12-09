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

import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtVisitor;

public abstract class CtVariableAccessImpl<T> extends CtExpressionImpl<T> implements CtVariableAccess<T> {
	private static final long serialVersionUID = 1L;

	CtVariableReference<T> variable;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtVariableAccess(this);
	}

	@Override
	public CtVariableReference<T> getVariable() {
		return variable;
	}

	@Override
	public <C extends CtVariableAccess<T>> C setVariable(CtVariableReference<T> variable) {
		if (variable != null) {
			variable.setParent(this);
		}
		this.variable = variable;
		return (C) this;
	}
}
