/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import java.util.List;

import static spoon.reflect.path.CtRole.ARGUMENT;
import static spoon.reflect.path.CtRole.EXECUTABLE_REF;

/**
 * This code element defines an abstract invocation on a
 * {@link spoon.reflect.declaration.CtExecutable}.
 *
 * @param <T>
 * 		Return type of this invocation
 */
public interface CtAbstractInvocation<T> extends CtElement {
	/**
	 * The arguments of the invocation.
	 *
	 * @return the expressions that define the values of the arguments
	 */
	@PropertyGetter(role = ARGUMENT)
	List<CtExpression<?>> getArguments();

	/**
	 * Adds an argument expression to the invocation.
	 */
	@PropertySetter(role = ARGUMENT)
	<C extends CtAbstractInvocation<T>> C addArgument(CtExpression<?> argument);

	/**
	 * Removes an argument expression from the invocation.
	 */
	@PropertySetter(role = ARGUMENT)
	void removeArgument(CtExpression<?> argument);

	/**
	 * Sets the invocation's arguments.
	 */
	@PropertySetter(role = ARGUMENT)
	<C extends CtAbstractInvocation<T>> C setArguments(List<CtExpression<?>> arguments);

	/**
	 * Returns the invoked executable.
	 */
	@PropertyGetter(role = EXECUTABLE_REF)
	CtExecutableReference<T> getExecutable();

	/**
	 * Sets the invoked executable.
	 */
	@PropertySetter(role = EXECUTABLE_REF)
	<C extends CtAbstractInvocation<T>> C setExecutable(CtExecutableReference<T> executable);
}
