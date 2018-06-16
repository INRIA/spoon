/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;

import static spoon.reflect.path.CtRole.EXPRESSION;

public class CtLiteralImpl<T extends Object> extends CtExpressionImpl<T> implements CtLiteral<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.VALUE)
	T value;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtLiteral(this);
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public <C extends CtLiteral<T>> C setValue(T value) {
		if (this.value instanceof CtElement) {
			((CtElement) this.value).setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, EXPRESSION, value, this.value);
		this.value = value;
		return (C) this;
	}

	@Override
	public CtLiteral<T> clone() {
		return (CtLiteral<T>) super.clone();
	}
}
