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
package spoon.reflect.reference;

import java.lang.reflect.Member;

import spoon.reflect.declaration.CtField;

/**
 * This interface defines a reference to a
 * {@link spoon.reflect.declaration.CtField}.
 */
public interface CtFieldReference<T> extends CtVariableReference<T> {
	/**
	 * Gets the runtime member that corresponds to a field reference if any.
	 *
	 * @return the member (null if not found)
	 */
	Member getActualField();

	CtField<T> getDeclaration();

	/**
	 * Gets the type in which the field is declared.
	 */
	CtTypeReference<?> getDeclaringType();

	/**
	 * Gets the qualified name of the field.
	 */
	String getQualifiedName();

	/**
	 * Tells if the referenced field is final.
	 */
	boolean isFinal();

	/**
	 * Tells if the referenced field is static.
	 */
	boolean isStatic();

	/**
	 * Sets the type in which the field is declared.
	 */
	<C extends CtFieldReference<T>> C setDeclaringType(CtTypeReference<?> declaringType);

	/**
	 * Forces a reference to a final element.
	 */
	<C extends CtFieldReference<T>> C setFinal(boolean b);

	/**
	 * Forces a reference to a static element.
	 */
	<C extends CtFieldReference<T>> C setStatic(boolean b);
}
