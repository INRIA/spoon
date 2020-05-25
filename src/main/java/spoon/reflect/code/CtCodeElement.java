/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.declaration.CtElement;

/**
 * This interface is the root interface of the code elements.
 */
public interface CtCodeElement extends CtElement {

	/**
	 * Partially evaluates an element and all its sub-elements.
	 *
	 * @param <R>
	 * 		the returned element
	 * @return the result of the partial evaluation
	 */
	<R extends CtCodeElement> R partiallyEvaluate();

	@Override
	CtCodeElement clone();
}
