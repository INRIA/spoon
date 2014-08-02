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

import spoon.reflect.reference.CtTypeReference;

/**
 * This element defines a type parameter (aka generics).
 */
public interface CtTypeParameter extends CtElement {

	/**
	 * Returns the bounds of this type parameter. These are the types given by
	 * the <i>extends</i> clause. If there is no explicit <i>extends</i> clause,
	 * then <tt>java.lang.Object</tt> is considered to be the sole bound.
	 *
	 * @return the List of bounds
	 */
	List<CtTypeReference<?>> getBounds();

	/**
	 * Returns the name of this type parameter.
	 *
	 * @return the name of this parameter
	 */
	String getName();

	/**
	 * Sets the bounds of this type parameter.
	 *
	 * @param bounds the Set of bounds to set
	 */
	void setBounds(List<CtTypeReference<?>> bounds);

	/**
	 * Adds a bound
	 * 
	 * @param bound the bound to add
	 * @return true if the bound was added
	 */
	boolean addBound(CtTypeReference<?> bound);

	/**
	 * Removes a bound
	 * 
	 * @param bound the bound to remove
	 * @return true if the bound was removed
	 */
	boolean removeBound(CtTypeReference<?> bound);

	/**
	 * Sets the name of this type parameter.
	 *
	 * @param name the new name
	 */
	void setName(String name);

}
