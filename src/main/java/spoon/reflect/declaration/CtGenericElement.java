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

import java.util.List;

import spoon.reflect.reference.CtTypeReference;

/**
 * This abstract element defines a declaration that accepts formal type
 * parameters (aka generics).
 */
public interface CtGenericElement extends CtElement {
	/**
	 * Returns the formal type parameters of this generic element.
	 */
	List<CtTypeReference<?>> getFormalTypeParameters();

	/**
	 * Sets the type parameters of this generic element.
	 */
	<T extends CtGenericElement> T setFormalTypeParameters(List<CtTypeReference<?>> formalTypeParameters);

	/**
	 * Add a type parameter to this generic element.
	 *
	 * @param formalTypeParameter
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	<T extends CtGenericElement> T addFormalTypeParameter(CtTypeReference<?> formalTypeParameter);

	/**
	 * Removes a type parameters from this generic element.
	 *
	 * @param formalTypeParameter
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	boolean removeFormalTypeParameter(CtTypeReference<?> formalTypeParameter);

}
