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
