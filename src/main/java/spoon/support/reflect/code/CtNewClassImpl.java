/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.CtVisitor;

import static spoon.reflect.path.CtRole.NESTED_TYPE;

public class CtNewClassImpl<T> extends CtConstructorCallImpl<T> implements CtNewClass<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = NESTED_TYPE)
	CtClass<?> anonymousClass;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtNewClass(this);
	}

	@Override
	public CtClass<?> getAnonymousClass() {
		return anonymousClass;
	}

	@Override
	public <N extends CtNewClass> N setAnonymousClass(CtClass<?> anonymousClass) {
		if (anonymousClass != null) {
			anonymousClass.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, NESTED_TYPE, anonymousClass, this.anonymousClass);
		this.anonymousClass = anonymousClass;
		return (N) this;
	}

	@Override
	public CtNewClass<T> clone() {
		return (CtNewClass<T>) super.clone();
	}
}
