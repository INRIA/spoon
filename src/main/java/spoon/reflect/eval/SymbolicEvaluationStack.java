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
import java.util.Map;
import java.util.Stack;

import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtVariableReference;

/**
 * This class defines a symbolic evaluation stack for
 * {@link spoon.reflect.eval.SymbolicEvaluator}.
 */
public class SymbolicEvaluationStack {

	/**
	 * Tests the equality with another stack.
	 */
	@Override
	public boolean equals(Object obj) {
		SymbolicEvaluationStack c = (SymbolicEvaluationStack) obj;
		return frameStack.equals(c.frameStack);
	}

	private Stack<SymbolicStackFrame> frameStack = new Stack<SymbolicStackFrame>();

	/**
	 * Constructs and returns the calling stack for this evaluation stack.
	 */
	public Stack<CtAbstractInvocation<?>> getCallingStack() {
		Stack<CtAbstractInvocation<?>> s = new Stack<CtAbstractInvocation<?>>();
		for (SymbolicStackFrame f : frameStack) {
			s.add(f.getInvocation());
		}
		return s;
	}

	/**
	 * Enters and creates a new {@link SymbolicStackFrame}, which is pushed on
	 * the top of the stack.
	 *
	 * @param caller
	 *            the invocation that starts this new frame
	 * @param target
	 *            the target of the invocation (to be <code>this</code>, see
	 *            {@link #getThis()})
	 * @param executable
	 *            the entered executable
	 * @param variables
	 *            the variables accessible from the frame (invocation's
	 *            parameters)
	 */
	public void enterFrame(CtAbstractInvocation<?> caller,
			SymbolicInstance<?> target, CtExecutableReference<?> executable,
			List<SymbolicInstance<?>> arguments,
			Map<CtVariableReference<?>, SymbolicInstance<?>> variables) {
		frameStack.push(new SymbolicStackFrame(caller,
				frameStack.isEmpty() ? null : frameStack.peek().getThis(),
				target, executable, arguments, variables));
	}

	/**
	 * Pops the top frame in order to leave it.
	 */
	public void exitFrame() {
		frameStack.pop();
	}

	/**
	 * Gets the this value of the top frame.
	 */
	public SymbolicInstance<?> getThis() {
		return frameStack.peek().getThis();
	}

	/**
	 * Gets the symbolic value of a variable within the top frame.
	 */
	public SymbolicInstance<?> getVariableValue(CtVariableReference<?> vref) {
		if (frameStack.peek().getVariables().containsKey(vref)) {
			return frameStack.peek().getVariables().get(vref);
		}
		throw new RuntimeException("unknown variable '" + vref + "'");
	}

	/**
	 * Sets the symbolic value of a variable within the top frame.
	 */
	public void setVariableValue(CtVariableReference<?> vref,
			SymbolicInstance<?> value) {
		if (frameStack.peek().getVariables().containsKey(vref)) {
			frameStack.peek().getVariables().put(vref, value);
		} else {
			throw new RuntimeException("unknown variable '" + vref + "'");
		}
	}

	/**
	 * Creates a copy of the given stack.
	 */
	public SymbolicEvaluationStack(SymbolicEvaluationStack stack) {
		for (SymbolicStackFrame f : stack.frameStack) {
			frameStack.add(new SymbolicStackFrame(f));
		}
	}

	/**
	 * Creates an empty evaluation stack.
	 */
	public SymbolicEvaluationStack() {
	}

	/**
	 * A string representation.
	 */
	@Override
	public String toString() {
		return "" + frameStack;
	}

	/**
	 * Dumps the stack on the screen.
	 */
	public void dump() {
		System.out.println("Stack:");
		int i = 1;
		for (SymbolicStackFrame f : frameStack) {
			System.out.print(" " + (i++) + "\t");
			System.out.println(f.toString());
		}
	}

	/**
	 * Gets the current result (returned value) for the top stack frame of this
	 * stack.
	 */
	public SymbolicInstance<?> getResult() {
		return frameStack.peek().getResult();
	}

	/**
	 * Sets the current result (returned value) for the top stack frame of this
	 * stack.
	 */
	public void setResult(SymbolicInstance<?> result) {
		frameStack.peek().setResult(result);
	}

	/**
	 * Gets the frames as a stack.
	 */
	public Stack<SymbolicStackFrame> getFrameStack() {
		return frameStack;
	}

}
