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

import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.support.DerivedProperty;
import spoon.reflect.annotations.MetamodelPropertyField;

public abstract class CtVariableAccessImpl<T> extends CtExpressionImpl<T> implements CtVariableAccess<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.VARIABLE)
	CtVariableReference<T> variable;

	@Override
	public CtVariableReference<T> getVariable() {
		if (variable != null) {
			return (CtVariableReference<T>) variable;
		}
		if (getFactory() != null) {
			CtVariableReference<Object> ref = getFactory().Core().createLocalVariableReference();
			ref.setParent(this);
			return (CtVariableReference<T>) ref;
		}
		return null;
	}

	@Override
	public <C extends CtVariableAccess<T>> C setVariable(CtVariableReference<T> variable) {
		if (variable != null) {
			variable.setParent(this);
		}
		this.variable = variable;
		return (C) this;
	}

	@Override
	@DerivedProperty
	public CtTypeReference<T> getType() {
		return getVariable().getType();
	}

	@Override
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
