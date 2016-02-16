/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.reflect.declaration;

import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;

/**
 * This element defines a type parameter (aka generics).
 */
public interface CtTypeParameter extends CtNamedElement {

	/**
	 * Returns the bounds of this type parameter. These are the types given by
	 * the <i>extends</i> clause. If there is no explicit <i>extends</i> clause,
	 * then <tt>java.lang.Object</tt> is considered to be the sole bound.
	 */
	@Deprecated
	List<CtTypeReference<?>> getBounds();

	/**
	 * Sets the bounds of this type parameter.
	 */
	@Deprecated
	<T extends CtTypeParameter> T setBounds(List<CtTypeReference<?>> bounds);

	/**
	 * @param bounds
	 * @return
	 */
	@Deprecated
	<T extends CtTypeParameter> T addBound(CtTypeReference<?> bounds);

	/**
	 * @param bounds
	 * @return
	 */
	@Deprecated
	boolean removeBound(CtTypeReference<?> bounds);

	/**
	 * A type parameter can have an <code>extends</code> clause which declare
	 * one ({@link CtTypeReference} or more ({@link CtIntersectionTypeReference} references.
	 */
	CtTypeReference<?> getSuperType();

	/**
	 * Sets the <code>extends</code> clause of the type parameter.
	 */
	<T extends CtTypeParameter> T setSuperType(CtTypeReference<?> superType);
}
