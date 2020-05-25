/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;

public class CtEnumValueImpl<T> extends CtFieldImpl<T> implements CtEnumValue<T> {
	@Override
	public void accept(CtVisitor v) {
		v.visitCtEnumValue(this);
	}

	@Override
	public CtEnumValue clone() {
		return (CtEnumValue) super.clone();
	}

	@DerivedProperty
	@Override
	public CtExpression<T> getAssignment() {
		return null;
	}
}
