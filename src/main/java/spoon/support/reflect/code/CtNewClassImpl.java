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

import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.CtVisitor;

public class CtNewClassImpl<T> extends CtConstructorCallImpl<T> implements CtNewClass<T> {
	private static final long serialVersionUID = 1L;

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
		anonymousClass.setParent(this);
		this.anonymousClass = anonymousClass;
		return (N) this;
	}
}
