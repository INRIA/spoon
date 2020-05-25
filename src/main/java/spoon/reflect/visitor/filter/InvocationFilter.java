/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.Filter;

/**
 * This simple filter matches all the accesses to a given executable or any
 * executable that overrides it.
 */
public class InvocationFilter implements Filter<CtInvocation<?>> {

	private CtExecutableReference<?> executable;

	/**
	 * Creates a new invocation filter.
	 *
	 * @param executable
	 * 		the executable to be tested for being invoked
	 */
	public InvocationFilter(CtExecutableReference<?> executable) {
		this.executable = executable;
	}

	/**
	 * Creates a new invocation filter.
	 *
	 * @param method
	 * 		the executable to be tested for being invoked.
	 */
	public InvocationFilter(CtMethod<?> method) {
		this(method.getReference());
	}

	@Override
	public boolean matches(CtInvocation<?> invocation) {
		return invocation.getExecutable().isOverriding(executable);
	}
}
