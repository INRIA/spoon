/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor.replace;

import spoon.reflect.declaration.CtElement;

/** Interface for the AST node replacement infrastructure. The implementing subclasses are generated in internal classes in {@link spoon.support.visitor.replace.ReplacementVisitor} */
public interface ReplaceListener<T extends CtElement> {
	void set(T replace);
}
