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
	 *
	 * @param <B> the body's type
	 *
	 * @return the body
	 */
	<B extends R> CtBlock<B> getBody();

	/**
	 * Gets the declaring type
	 *
	 * @return the declaring type
	 */
	CtType<?> getDeclaringType();

	/**
	 * Gets the parameters list.
	 *
	 * @return the List of parameters
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
	 *
	 * @return the Set of thrown types
	 */
	Set<CtTypeReference<? extends Throwable>> getThrownTypes();

	/**
	 * Sets the body expression.
	 *
	 * @param <B> the body's type
	 * @param body the body to set
	 */
	<B extends R> void setBody(CtBlock<B> body);

	/**
	 * Sets the parameters.
	 *
	 * @param parameters the Set of parameters to set
	 */
	void setParameters(List<CtParameter<?>> parameters);

	/**
	 * Add a parameter for this executable
	 * 
	 * @param parameter the parameter to add
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	boolean addParameter(CtParameter<?> parameter);

	/**
	 * Remove a parameter for this executable
	 * 
	 * @param parameter the parameter to remove
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	boolean removeParameter(CtParameter<?> parameter);

	/**
	 * Sets the thrown types.
	 *
	 * @param thrownTypes the Set of throwable types to set
	 */
	void setThrownTypes(Set<CtTypeReference<? extends Throwable>> thrownTypes);

	/**
	 * add a thrown type.
	 * 
	 * @param throwType the thrown type to add
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	boolean addThrownType(CtTypeReference<? extends Throwable> throwType);

	/**
	 * remove a thrown type.
	 * 
	 * @param throwType the thrown type to remove
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	boolean removeThrownType(CtTypeReference<? extends Throwable> throwType);

}