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
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.Filter;

/**
 * Gets all overriding method from the method given.
 */
public class OverridingMethodFilter implements Filter<CtMethod<?>> {
	private final CtMethod<?> method;

	/**
	 * Creates a new overriding method filter.
	 *
	 * @param method
	 * 		the executable to be tested for being invoked
	 */
	public OverridingMethodFilter(CtMethod<?> method) {
		this.method = method;
	}

	@Override
	public boolean matches(CtMethod<?> element) {
		final CtType expectedParent = method.getParent(CtType.class);
		final CtType<?> currentParent = element.getParent(CtType.class);
		return currentParent.isSubtypeOf(expectedParent.getReference()) //
				&& !currentParent.equals(expectedParent) //
				&& element.getReference().isOverriding(method.getReference());
	}
}
