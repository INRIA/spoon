/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import spoon.reflect.Factory;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.eval.PartialEvaluator;
import spoon.reflect.eval.SymbolicEvaluationPath;
import spoon.reflect.eval.SymbolicEvaluator;
import spoon.reflect.eval.SymbolicEvaluatorObserver;
import spoon.reflect.eval.SymbolicInstance;
import spoon.reflect.eval.observer.SymbolicEvaluationPathsMaker;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.eval.VisitorPartialEvaluator;
import spoon.support.reflect.eval.VisitorSymbolicEvaluator;

/**
 * A factory to create some evaluation utilities on the Spoon metamodel.
 */
public class EvalFactory extends SubFactory {

	private static final long serialVersionUID = 1L;

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

	/**
	 * Creates a symbolic evaluator on the Spoon meta-model.
	 * 
	 * @param observers
	 *            the observers to be notified of the the evaluation progress
	 */
	public SymbolicEvaluator createSymbolicEvaluator(
			SymbolicEvaluatorObserver... observers) {
		return new VisitorSymbolicEvaluator(observers);
	}

	/**
	 * Creates a new symbolic instance.
	 * 
	 * @param evaluator
	 *            the evaluator
	 * @param concreteType
	 *            the type of the instance
	 * @param isType
	 *            tells if it is a type instance or a regular instance
	 */
	public <T> SymbolicInstance<T> createSymbolicInstance(
			SymbolicEvaluator evaluator, CtTypeReference<T> concreteType,
			boolean isType) {
		return new SymbolicInstance<T>(evaluator, concreteType, isType);
	}

	/**
	 * Gets the symbolic evaluation paths of the program, as calculated by
	 * {@link spoon.reflect.eval.SymbolicEvaluator}.
	 * 
	 * @param entryPoints
	 *            the entry point methods
	 * @return a map containing the paths for each entry point
	 */
	@SuppressWarnings("unchecked")
	public Map<CtMethod<?>, Collection<SymbolicEvaluationPath>> createSymbolicEvaluationPaths(
			Collection<CtMethod<?>> entryPoints) {
		Map<CtMethod<?>, Collection<SymbolicEvaluationPath>> results = new HashMap<CtMethod<?>, Collection<SymbolicEvaluationPath>>();
		for (CtMethod<?> m : entryPoints) {
			SymbolicEvaluationPathsMaker pathsMaker = new SymbolicEvaluationPathsMaker();
			SymbolicEvaluator evaluator = createSymbolicEvaluator(pathsMaker);
			evaluator.invoke(m);
			results.put(m, pathsMaker.getPaths());
		}
		return results;
	}
}