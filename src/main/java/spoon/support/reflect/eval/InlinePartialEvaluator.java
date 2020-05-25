/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.eval;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.eval.PartialEvaluator;
import spoon.reflect.visitor.CtScanner;

/**
 * Simplifies an AST inline based on {@link VisitorPartialEvaluator} (wanring: the nodes are changed).
 */
public class InlinePartialEvaluator extends CtScanner {
	private final PartialEvaluator eval;

	public InlinePartialEvaluator(PartialEvaluator eval) {
		this.eval = eval;
	}
	@Override
	protected void exit(CtElement e) {
		CtElement simplified = eval.evaluate(e);
		if (simplified != null) {
			e.replace(simplified);
		}
	}
}
