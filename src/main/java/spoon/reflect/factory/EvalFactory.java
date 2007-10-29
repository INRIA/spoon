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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spoon.reflect.Factory;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.eval.SymbolicEvaluatorObserver;
import spoon.reflect.eval.PartialEvaluator;
import spoon.reflect.eval.SymbolicEvaluationPath;
import spoon.reflect.eval.SymbolicEvaluator;
import spoon.reflect.eval.SymbolicInstance;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
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
	 */
	public SymbolicEvaluator createSymbolicEvaluator() {
		return new VisitorSymbolicEvaluator();
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
	public void evaluate(
			Collection<CtMethod<Void>> entryPoints, SymbolicEvaluatorObserver... observers) {
		for (CtMethod<?> m : entryPoints) {
			SymbolicEvaluator evaluator = createSymbolicEvaluator();
			evaluator.addObservers(Arrays.asList(observers));
			List<SymbolicInstance<?>> args = new ArrayList<SymbolicInstance<?>>();
			for (CtParameter<?> p : m.getParameters()) {
				SymbolicInstance arg = createSymbolicInstance(evaluator, p
						.getType(), false);
				evaluator.getHeap().store(arg);
				args.add(arg);
			}
			// Create target(this) for the invocation
			SymbolicInstance target = createSymbolicInstance(evaluator, m
					.getDeclaringType().getReference(), m.getModifiers()
					.contains(ModifierKind.STATIC));
			// Seed the fields of the class
			CtType<?> targetType = m.getDeclaringType();
			for (CtField field : targetType.getFields()) {
				if (!field.getModifiers().contains(ModifierKind.STATIC)
						&& m.getModifiers().contains(ModifierKind.STATIC)) {
					continue;
				}

				CtVariableReference<?> fref = field.getReference();
				SymbolicInstance si = createSymbolicInstance(evaluator, fref
						.getType(), false);
				target.setFieldValue(evaluator.getHeap(), fref, si);
			}

			evaluator.getHeap().store(target);
			try {
				evaluator.invoke(target, m, args);
			} catch (Throwable th) {
				th.printStackTrace();
			}
			// paths.put(m, evaluator.getPaths());
		}
		// return paths;
	}
}