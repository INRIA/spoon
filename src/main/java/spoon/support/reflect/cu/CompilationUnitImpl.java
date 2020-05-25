/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.cu;

import spoon.reflect.cu.CompilationUnit;
import spoon.support.reflect.declaration.CtCompilationUnitImpl;

/**
 * Implements a compilation unit. In Java, a compilation unit can contain only one
 * public type declaration and other secondary types declarations (not public).
 */
public class CompilationUnitImpl extends CtCompilationUnitImpl implements CompilationUnit {
	private static final long serialVersionUID = 2L;

	private boolean autoImport = true;
}
