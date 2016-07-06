/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
