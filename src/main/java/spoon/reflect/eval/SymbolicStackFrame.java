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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtVariableReference;

/**
 * This class represents a frame of a symbolic evaluation stack.
 */
public class SymbolicStackFrame {

	/**
	 * Copies a frame from a given one (does not clone stateless info).
	 */
	public SymbolicStackFrame(SymbolicStackFrame frame) {
		for (Entry<CtVariableReference<?>, SymbolicInstance<?>> e : frame.variables
				.entrySet()) {
			SymbolicInstance<?> i = e.getValue();
			variables.put(e.getKey(), i == null ? null : i.getClone());
		}
		caller = frame.caller == null ? null : frame.caller.getClone();
		target = frame.target == null ? null : frame.target.getClone();
		result = frame.result == null ? null : frame.result.getClone();
		invocation = frame.invocation;
		executable = frame.executable;
		if (frame.arguments != null) {
			arguments = new ArrayList<SymbolicInstance<?>>();
			for (SymbolicInstance<?> i : frame.arguments) {
				arguments.add(i == null ? null : i.getClone());
			}
		}
	}

	/**
	 * Frame equality.
	 */
	@Override
	public boolean equals(Object obj) {
		SymbolicStackFrame f = (SymbolicStackFrame) obj;
		return (target == null ? target == f.target : target.equals(f.target))
				&& invocation == f.invocation
				&& executable.equals(f.executable)
				&& variables.equals(f.variables);
	}

	private Map<CtVariableReference<?>, SymbolicInstance<?>> variables = new HashMap<CtVariableReference<?>, SymbolicInstance<?>>();

	private SymbolicInstance<?> target;

	private SymbolicInstance<?> caller;

	private CtAbstractInvocation<?> invocation;

	private CtExecutableReference<?> executable;

	/**
	 * Gets the parent invocation of this frame.
	 */
	public CtAbstractInvocation<?> getInvocation() {
		return invocation;
	}

	/**
	 * Gets the <code>this</code> value.
	 */
	public SymbolicInstance<?> getThis() {
		return target;
	}

	/**
	 * Gets the calling instance if applicable.
	 */
	public SymbolicInstance<?> getCaller() {
		return caller;
	}

	/**
	 * Gets the local variables defined for this frame.
	 */
	public Map<CtVariableReference<?>, SymbolicInstance<?>> getVariables() {
		return variables;
	}

	List<SymbolicInstance<?>> arguments;

	/**
	 * Return the arguments (also present in the variables).
	 */
	public List<SymbolicInstance<?>> getArguments() {
		return arguments;
	}

	/**
	 * Creates a new evalutation stack frame.
	 * 
	 * @param invocation
	 *            the parent invocation
	 * @param caller
	 *            the caller
	 * @param instance
	 *            the target (this) of the frame ({@link #getThis()})
	 * @param executable
	 *            the executable that corresponds to this frame
	 * @param variables
	 *            the local variables defined within this frame (should be a
	 *            copy)
	 */
	public SymbolicStackFrame(CtAbstractInvocation<?> invocation, SymbolicInstance<?> caller,
			SymbolicInstance<?> instance, CtExecutableReference<?> executable,
			List<SymbolicInstance<?>> arguments,
			Map<CtVariableReference<?>, SymbolicInstance<?>> variables) {
		super();
		this.variables = variables;
		this.caller = caller;
		this.target = instance;
		this.invocation = invocation;
		this.executable = executable;
		this.arguments = arguments;
	}

	/**
	 * A string representation.
	 */
	@Override
	public String toString() {
		return "" + executable.getDeclaringType().getSimpleName()
				+ CtExecutable.EXECUTABLE_SEPARATOR
				+ executable.getSimpleName() + " on " + target + " variables="
				+ variables + " arguments=" + arguments + " result=" + result;
	}

	/**
	 * Gets the executable of the frame.
	 */
	public CtExecutableReference<?> getExecutable() {
		return executable;
	}

	private SymbolicInstance<?> result = null;

	/**
	 * Gets the result of this frame execution.
	 */
	public SymbolicInstance<?> getResult() {
		return result;
	}

	/**
	 * Sets the result of this frame execution.
	 */
	public void setResult(SymbolicInstance<?> result) {
		this.result = result;
	}

}
