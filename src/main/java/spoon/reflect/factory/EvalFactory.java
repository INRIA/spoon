/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.factory;

import spoon.reflect.eval.PartialEvaluator;
import spoon.support.reflect.eval.VisitorPartialEvaluator;

/**
 * A factory to create some evaluation utilities on the Spoon metamodel.
 */
public class EvalFactory extends SubFactory {

	/**
	 * Creates the evaluation factory.
	 */
	public EvalFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Creates a partial evaluator on the Spoon meta-model.
	 */
	public PartialEvaluator createPartialEvaluator() {
		return new VisitorPartialEvaluator();
	}
}
