/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.reference;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.declaration.CtField;
import spoon.reflect.path.CtRole;
import spoon.support.DerivedProperty;

import java.lang.reflect.Member;


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

	@Override
	@DerivedProperty
	CtField<T> getDeclaration();

	/**
	 * Returns the {@link CtField} that corresponds to the reference
	 * even if its declaring type isn't in the Spoon source path  (in this case,
	 * the Spoon elements are built with runtime reflection)
	 *
	 * @return the field declaration that corresponds to the reference.
	 */
	@DerivedProperty
	CtField<T> getFieldDeclaration();

	/**
	 * Gets the type in which the field is declared.
	 */
	@PropertyGetter(role = CtRole.DECLARING_TYPE)
	CtTypeReference<?> getDeclaringType();

	/**
	 * Gets the qualified name of the field.
	 */
	String getQualifiedName();

	/**
	 * Tells if the referenced field is final.
	 */
	@PropertyGetter(role = CtRole.IS_FINAL)
	boolean isFinal();

	/**
	 * Tells if the referenced field is static.
	 */
	@PropertyGetter(role = CtRole.IS_STATIC)
	boolean isStatic();

	/**
	 * Sets the type in which the field is declared.
	 */
	@PropertySetter(role = CtRole.DECLARING_TYPE)
	<C extends CtFieldReference<T>> C setDeclaringType(CtTypeReference<?> declaringType);

	/**
	 * Forces a reference to a final element.
	 */
	@PropertySetter(role = CtRole.IS_FINAL)
	<C extends CtFieldReference<T>> C setFinal(boolean b);

	/**
	 * Forces a reference to a static element.
	 */
	@PropertySetter(role = CtRole.IS_STATIC)
	<C extends CtFieldReference<T>> C setStatic(boolean b);

	@Override
	CtFieldReference<T> clone();
}
