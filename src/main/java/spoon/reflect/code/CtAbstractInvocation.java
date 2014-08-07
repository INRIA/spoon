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

package spoon.reflect.code;

import java.util.List;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtExecutableReference;

/**
 * This code element defines an abstract invocation on a
 * {@link spoon.reflect.declaration.CtExecutable}.
 * 
 * @param <T>
 *            Return type of this invocation
 */
public interface CtAbstractInvocation<T> extends CtElement {
	/**
	 * The arguments of the invocation.
	 * 
	 * @return the expressions that define the values of the arguments
	 */
	List<CtExpression<?>> getArguments();

	/**
	 * Adds an argument expression to the invocation.
	 *
	 * @param argument the argument expression to set
	 */
	void addArgument(CtExpression<?> argument);

	/**
	 * Removes an argument expression from the invocation.
	 *
	 * @param argument the argument expression to remove
	 */
	void removeArgument(CtExpression<?> argument);

	/**
	 * Returns the invoked executable.
	 *
	 * @return the executable reference
	 */
	CtExecutableReference<T> getExecutable();

	/**
	 * Sets the invocation's arguments.
	 *
	 * @param arguments a List of argument expressions to set
	 */
	void setArguments(List<CtExpression<?>> arguments);

	/**
	 * Sets the invoked executable.
	 *
	 * @param executable the executable reference to set
	 */
	void setExecutable(CtExecutableReference<T> executable);
}
