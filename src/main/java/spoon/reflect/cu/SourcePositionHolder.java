/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.cu;

import spoon.support.Experimental;
import spoon.support.sniper.internal.SourceFragment;

/**
 * This interface represents an element which knows its position in a source file.
 */
public interface SourcePositionHolder {
	/** If the element comes from a Java source file (hence has not created during transformation), returns the position in the original source file */
	SourcePosition getPosition();

	/**
	 * Returns the original source code (maybe different from toString() if a transformation has been applied).
	 *
	 * Warning: this is a advanced method which cannot be considered as part of the stable API
	 *
	 */
	@Experimental
	SourceFragment getOriginalSourceFragment();
}
