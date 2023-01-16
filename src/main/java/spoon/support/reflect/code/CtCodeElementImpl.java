/*
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
	private static final String EVAL_KEY = "##PARTIAL_EVAL_RES##";

	public CtCodeElementImpl() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R extends CtCodeElement> R partiallyEvaluate() {
		var existingMetadata = this.getMetadata(EVAL_KEY);
		if (existingMetadata != null) {
			// We do a dirty check to make sure we don't have stale info.
			if (getMetadata(META_DIRTY_KEY) != (Boolean)true) {
				return (R) this.getMetadata(EVAL_KEY);
			}
		}

		VisitorPartialEvaluator eval = new VisitorPartialEvaluator();

		R value = eval.evaluate((R) this);
		this.putMetadata(EVAL_KEY, value);
		this.putMetadata(META_DIRTY_KEY, false);
		return value;
	}

	@Override
	public CtCodeElement clone() {
		return (CtCodeElement) super.clone();
	}
}
