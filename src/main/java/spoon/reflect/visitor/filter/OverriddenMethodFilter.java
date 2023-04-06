/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Filter;
import spoon.support.adaption.TypeAdaptor;

/**
 * Gets all overridden method from the method given.
 */
public class OverriddenMethodFilter implements Filter<CtMethod<?>> {
	private final CtMethod<?> method;
	private final TypeAdaptor context;
	private boolean includingSelf = false;

	/**
	 * Creates a new overridden method filter.
	 *
	 * @param method
	 * 		the executable to be tested for being invoked
	 */
	public OverriddenMethodFilter(CtMethod<?> method) {
		this.method = method;
		context = new TypeAdaptor(method.getDeclaringType());
	}

	/**
	 * @param includingSelf if false then element which is equal to the #method is not matching.
	 * false is default behavior
	 */
	public OverriddenMethodFilter includingSelf(boolean includingSelf) {
		this.includingSelf = includingSelf;
		return this;
	}

	@Override
	public boolean matches(CtMethod<?> element) {
		if (method == element) {
			return this.includingSelf;
		}
		return context.isOverriding(method, element);
	}
}
