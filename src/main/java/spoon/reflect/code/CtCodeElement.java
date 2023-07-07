/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
	 * @param <R> the type of the returned element
	 * @return the result of the partial evaluation.
	 *         The element is always cloned, even if nothing has been evaluated.
	 * @see spoon.support.reflect.eval.VisitorPartialEvaluator
	 */
	<R extends CtCodeElement> R partiallyEvaluate();

	@Override
	CtCodeElement clone();
}
