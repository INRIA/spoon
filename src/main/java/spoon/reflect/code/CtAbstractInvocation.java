/**
 * Copyright (C) 2006-2017 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
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
package spoon.reflect.code;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import java.util.List;

import static spoon.reflect.path.CtRole.ARGUMENT;
import static spoon.reflect.path.CtRole.EXECUTABLE;

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
	@PropertyGetter(role = EXECUTABLE)
	CtExecutableReference<T> getExecutable();

	/**
	 * Sets the invoked executable.
	 */
	@PropertySetter(role = EXECUTABLE)
	<C extends CtAbstractInvocation<T>> C setExecutable(CtExecutableReference<T> executable);
}
