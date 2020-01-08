/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.refactoring.invocation;

import java.util.Collection;
import java.util.HashSet;

import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;

public class MethodCallState {
	private CtExecutable<?> method;
	private Collection<CtExecutable<?>> callerMethods;
	private Collection<CtType<?>> callerFields;

	/**
	 * @param method
	 */
	public MethodCallState(CtExecutable<?> method) {
		this.method = method;
		this.callerFields = new HashSet<>();
		this.callerMethods = new HashSet<>();
	}

	/**
	 * @param Method invoking the method.
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public void add(CtExecutable<?> e) {
		callerMethods.add(e);
	}

	/**
	 * @param Field invoking the method with an initializer.
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public void add(CtType<?> e) {
		callerFields.add(e);
	}

	/**
	 * @return the method
	 */
	public CtExecutable<?> getMethod() {
		return method;
	}

	/**
	 * @return the callerFields
	 */
	public Collection<CtType<?>> getCallerFields() {
		return callerFields;
	}

	/**
	 * @return the callerMethods
	 */
	public Collection<CtExecutable<?>> getCallerMethods() {
		return callerMethods;
	}

	/**
	 * Checks the call state for the method.
	 *
	 * @return True if the method has no known call, false otherwise.
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean checkCallState() {
		return callerMethods.isEmpty() && callerFields.isEmpty();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Collection#contains(java.lang.Object)
	 */

	public boolean contains(Object o) {
		return callerFields.contains(o);
	}

	/**
	 * @param o
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public void remove(CtField<?> o) {
		callerFields.remove(o);
	}

	/**
	 * @param o
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public void remove(CtExecutable<?> o) {
		callerMethods.remove(o);
	}
}
