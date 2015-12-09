/*
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
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.Filter;

public class OverridingMethodFilter implements Filter<CtMethod<?>> {
	private final CtExecutableReference<?> executableReference;

	public OverridingMethodFilter(CtExecutableReference<?> executableReference) {
		this.executableReference = executableReference;
	}

	@Override
	public boolean matches(CtMethod<?> element) {
		final CtExecutable<?> declaration = executableReference.getDeclaration();
		if (declaration == null) {
			return false;
		}
		final CtType expectedParent = declaration.getParent(CtType.class);
		final CtType<?> currentParent = element.getParent(CtType.class);
		return currentParent.isAssignableFrom(expectedParent.getReference()) //
				&& !currentParent.equals(expectedParent) //
				&& element.getReference().isOverriding(executableReference);
	}
}
