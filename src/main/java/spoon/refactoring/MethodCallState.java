/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.refactoring;

import java.util.ArrayList;
import java.util.Collection;

import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtType;

/**
 * This class is for the call state of a method. A method can be called by
 * fields in a type e.g. class or by methods. Both cases are handled in this
 * class. For checking calls by fields use methods using CtType, for fields use
 * the methods using CtExecutable. A method is never called if both collections
 * are empty.
 */
public class MethodCallState {
	private CtExecutable<?> method;
	private Collection<CtExecutable<?>> callerMethods;
	private Collection<CtType<?>> callerFields;

	/**
	 *
	 * @param method for saving it's call state.
	 */
	public MethodCallState(CtExecutable<?> method) {
		this.method = method;
		this.callerFields = new ArrayList<>();
		this.callerMethods = new ArrayList<>();
	}

	/**
	 * Adds a CtExecutable to the methods invoking this method. Adding the same
	 * method again doesn't change the state.
	 *
	 * @param method invoking the method.
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public void add(CtExecutable<?> method) {
		callerMethods.add(method);
	}

	/**
	 * Adds a CtType to the fields invoking this method. Adding the same CtType
	 * again doesn't change the state.
	 *
	 * @param type invoking the method with an initializer.
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public void add(CtType<?> type) {
		callerFields.add(type);
	}

	/**
	 * Getter for the method, without saved call state. Returns the CtExecutable and
	 * not a copy.
	 *
	 * @return method without saved call state.
	 */
	public CtExecutable<?> getMethod() {
		return method;
	}

	/**
	 * Returns a collection containing all types invoking the method with a field.
	 * Even if a CtType invokes multiple times with different fields the method, the
	 * type is only present once. Returns the collection and not a copy. Changes to
	 * collection are directly backed in the state.
	 *
	 * @return Collection containing all types invoking the method with a field.
	 */
	public Collection<CtType<?>> getCallerFields() {
		return callerFields;
	}

	/**
	 * Returns a collection containing all CtExecutable invoking the method. Even if
	 * a CtExecutable invokes multiple times the method, the CtExecutable is only
	 * present once. Returns the collection and not a copy. Changes to collection
	 * are directly backed in the state.
	 *
	 * @return Collection containing all CtExecutable invoking the method.
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

	public boolean contains(CtType<?> o) {
		return callerFields.contains(o);
	}

	public boolean contains(CtExecutable<?> o) {
		return callerMethods.contains(o);
	}

	public void remove(CtType<?> o) {
		callerFields.remove(o);
	}

	public void remove(CtExecutable<?> o) {
		callerMethods.remove(o);
	}
}
