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

package spoon.support.reflect.eval;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.eval.SymbolicEvaluationStack;
import spoon.reflect.eval.SymbolicInstance;

public class SymbolicWrappedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private SymbolicInstance<? extends Throwable> cause;

	@SuppressWarnings("unchecked")
	public SymbolicWrappedException(
			SymbolicInstance<? extends Throwable> cause, CtElement element,
			SymbolicEvaluationStack stack) {
		this.cause = cause;
		this.element = element;
		this.stack = new SymbolicEvaluationStack(stack);
	}

	CtElement element;

	public CtElement getElement() {
		return element;
	}

	public void setElement(CtElement element) {
		this.element = element;
	}

	SymbolicEvaluationStack stack;

	public SymbolicEvaluationStack getStack() {
		return stack;
	}

	public void setStack(SymbolicEvaluationStack stack) {
		this.stack = stack;
	}

	public SymbolicInstance<? extends Throwable> getAbstractCause() {
		return cause;
	}

	public void setCause(SymbolicInstance<? extends Throwable> cause) {
		this.cause = cause;
	}
}
