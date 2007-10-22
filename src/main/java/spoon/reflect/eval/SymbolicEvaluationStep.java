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
 * This class defines a symbolic evaluation step for a given
 * {@link spoon.reflect.eval.SymbolicEvaluator}. a symbolic evaluation step
 * contains a copy of the interpretor's state when the step happened.
 */
public class SymbolicEvaluationStep {

	SymbolicStackFrame frame;

	SymbolicHeap heap;

	StepKind kind;

	/**
	 * Creates a new step.
	 * 
	 * @param kind
	 *            the step kind
	 * @param frame
	 *            the stack frame (should be a copy)
	 * @param heap
	 *            the heap (should be a copy)
	 */
	public SymbolicEvaluationStep(StepKind kind, SymbolicStackFrame frame,
			SymbolicHeap heap) {
		super();
		this.frame = frame;
		this.heap = heap;
		this.kind = kind;
	}

	/**
	 * Gets the stack frame that corresponds to this state.
	 */
	public SymbolicStackFrame getFrame() {
		return frame;
	}

	/**
	 * Gets the heap that corresponds to this state.
	 */
	public SymbolicHeap getHeap() {
		return heap;
	}

	/**
	 * A string representation.
	 */
	@Override
	public String toString() {
		return kind.toString() + " " + frame.toString() + " heap="
				+ heap.toString();
	}

	/**
	 * Gets the step kind.
	 */
	public StepKind getKind() {
		return kind;
	}

	/**
	 * Gets a symbolic instance from its id at the current evaluation step.
	 * 
	 * @return null if not found
	 */
	public SymbolicInstance<?> get(String id) {
		SymbolicInstance<?> res = heap.get(id);
		if (res != null)
			return res;
		for (SymbolicInstance<?> i : frame.getVariables().values()) {
			if (i != null && i.getId().equals(id)) {
				return i;
			}
		}
		return null;
	}

}
