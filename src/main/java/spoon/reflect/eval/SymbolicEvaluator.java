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

package spoon.reflect.eval;

import java.util.List;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.reference.CtTypeReference;

/**
 * This interface defines a symbolic evaluator for the Spoon meta-model.
 */
public interface SymbolicEvaluator {

	/**
	 * Returns the list of external classes that should be handled as statefull
	 * beans.
	 */
	List<CtTypeReference> getStatefullExternals();

	/**
	 * Gets all the evaluation paths resulting from an evaluation.
	 */
	List<SymbolicEvaluationPath> getPaths();

	/**
	 * Dumps the evaluation paths.
	 */
	void dumpPaths();

	/**
	 * Resets the state of this symbolic evaluator.
	 */
	void reset();

	/**
	 * Starts a symbolic evaluation by invoking a given static executable.
	 * 
	 * @param executable
	 *            to be invoked symbolically
	 * @param args
	 *            the arguments of the call (as symbolic instances)
	 */
	void invoke(CtExecutable executable, SymbolicInstance... args);

	/**
	 * Starts a symbolic evaluation by invoking a given executable.
	 * 
	 * @param target
	 *            the target instance
	 * @param executable
	 *            to be invoked
	 * @param args
	 *            the arguments of the call (as symbolic instances)
	 */
	void invoke(SymbolicInstance target, CtExecutable executable,
			List<SymbolicInstance> args);

	/**
	 * Gets the heap of the current symbolic evaluation step.
	 */
	SymbolicHeap getHeap();

	/**
	 * Gets the stack of the symbolic abstract evaluation step.
	 */
	SymbolicEvaluationStack getStack();

	/**
	 * Evaluates the given meta-model element in the current context of the
	 * evaluator.
	 */
	SymbolicInstance evaluate(CtElement element);
}