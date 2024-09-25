/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

import org.jspecify.annotations.Nullable;
import spoon.reflect.cu.SourcePositionHolder;
import spoon.reflect.path.CtRole;

/**
 * Represents an action of PrettyPrinter
 */
public interface PrinterEvent  {

	/**
	 * @return role of printed source code of element in scope of its parent
	 */
	CtRole getRole();

	/**
	 * We have a source fragment of to be printed element.
	 * Print unmodified parts of this source `fragment`
	 * @param fragment
	 * @param isModified true if at least some part of `SourceFragment` is modified.
	 */
	void printSourceFragment(SourceFragment fragment, ModificationStatus isModified);

	/**
	 * @return printed element or null if printing a primitive token
	 */
	@Nullable SourcePositionHolder getElement();

}
