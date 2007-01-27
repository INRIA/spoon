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

/**
 * This enum defines the kinds of
 * {@link spoon.reflect.eval.SymbolicEvaluationStep}s that can be encountered.
 */
public enum StepKind {

	/**
	 * Corresponds to an abstract evaluation step when the evaluator enters a
	 * new {@link SymbolicStackFrame} (method invocation).
	 */
	ENTER,

	/**
	 * Corresponds to an abstract evaluation step when the evaluator exits the
	 * current {@link SymbolicStackFrame} (method return).
	 */
	EXIT,

	/**
	 * For matching both {@link #ENTER} and {@link #EXIT}.
	 */
	BOTH

}
