/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
package spoon.reflect.cu;

import spoon.support.sniper.internal.ElementSourceFragment;
import spoon.support.Experimental;

/**
 * This interface represents an element which knows its position in a source file.
 */
public interface SourcePositionHolder {
	/** If the element comes from a Java source file (hence has not created during transformation), returns the position in the original source file */
	SourcePosition getPosition();

	/**
	 * Returns the original source code (maybe different from toString() if a transformation has been applied.
	 * Or {@link ElementSourceFragment#NO_SOURCE_FRAGMENT} if this element has no original source fragment.
	 *
	 * Warning: this is a advanced method which cannot be considered as part of the stable API
	 *
	 */
	@Experimental
	default ElementSourceFragment getOriginalSourceFragment() {
		return ElementSourceFragment.NO_SOURCE_FRAGMENT;
	}
}
