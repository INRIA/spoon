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

package spoon.reflect.declaration;

import java.util.List;
import java.util.Set;

import spoon.reflect.code.CtBlock;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * This element represents an executable element such as a method, a
 * constructor, or an anonymous block.
 */
public interface CtExecutable<R> extends CtNamedElement, CtGenericElement,
		CtTypedElement<R> {

	/**
	 * The separator for a string representation of an executable.
	 */
	public static final String EXECUTABLE_SEPARATOR = "#";

	/**
	 * Gets the body expression.
	 */
	<B extends R> CtBlock<B> getBody();

	/**
	 * Gets the declaring type
	 */
	CtType<?> getDeclaringType();

	/**
	 * Gets the parameters list.
	 */
	List<CtParameter<?>> getParameters();

	/*
	 * (non-Javadoc)
	 * 
	 * @see spoon.reflect.declaration.CtNamedElement#getReference()
	 */
	CtExecutableReference<R> getReference();

	/**
	 * Returns the exceptions and other throwables listed in this method or
	 * constructor's <tt>throws</tt> clause.
	 */
	Set<CtTypeReference<? extends Throwable>> getThrownTypes();

	/**
	 * Sets the body expression.
	 */
	<B extends R> void setBody(CtBlock<B> body);

	/**
	 * Sets the parameters.
	 */
	void setParameters(List<CtParameter<?>> parameters);

	/**
	 * Sets the thrown types.
	 */
	void setThrownTypes(Set<CtTypeReference<? extends Throwable>> thrownTypes);

}