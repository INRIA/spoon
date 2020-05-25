/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.code.CtCodeElement;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.reflect.eval.VisitorPartialEvaluator;

public abstract class CtCodeElementImpl extends CtElementImpl implements CtCodeElement {
	private static final long serialVersionUID = 1L;

	public CtCodeElementImpl() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R extends CtCodeElement> R partiallyEvaluate() {
		VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
		return eval.evaluate((R) this);
	}

	@Override
	public CtCodeElement clone() {
		return (CtCodeElement) super.clone();
	}
}
