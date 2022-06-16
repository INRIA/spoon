/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor.java.internal;

import spoon.reflect.declaration.CtModule;

public class ModuleRuntimeBuilderContext extends AbstractRuntimeBuilderContext {
	private final CtModule ctModule;

	public ModuleRuntimeBuilderContext(CtModule ctModule) {
		super(ctModule);
		this.ctModule = ctModule;
	}

	/**
	 * Returns the module belonging to this context.
	 *
	 * @return the package of this context
	 */
	public CtModule getModule() {
		return ctModule;
	}
}
