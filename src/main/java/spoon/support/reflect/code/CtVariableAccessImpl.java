/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.support.DerivedProperty;

import static spoon.reflect.path.CtRole.VARIABLE;

public abstract class CtVariableAccessImpl<T> extends CtExpressionImpl<T> implements CtVariableAccess<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = VARIABLE)
	CtVariableReference<T> variable;

	@Override
	public CtVariableReference<T> getVariable() {
		if (variable == null && getFactory() != null) {
			variable = getFactory().Core().createLocalVariableReference();
			variable.setParent(this);
		}
		return variable;
	}

	@Override
	public <C extends CtVariableAccess<T>> C setVariable(CtVariableReference<T> variable) {
		if (variable != null) {
			variable.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, VARIABLE, variable, this.variable);
		this.variable = variable;
		return (C) this;
	}

	@Override
	@DerivedProperty
	public CtTypeReference<T> getType() {
		return getVariable().getType();
	}

	@Override
	@DerivedProperty
	public <C extends CtTypedElement> C setType(CtTypeReference<T> type) {
		if (type != null) {
			type.setParent(this);
		}
		if (type != null) {
			getVariable().setType(type);
		}
		return (C) this;
	}

	@Override
	public CtVariableAccess<T> clone() {
		return (CtVariableAccess<T>) super.clone();
	}
}
