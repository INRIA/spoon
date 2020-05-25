/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.support.DerivedProperty;

/**
 * This interface represents a member of a class (field, method,
 * nested class or static/instance initializer).
 */
public interface CtTypeMember extends CtModifiable, CtNamedElement {

	/**
	 * Gets the type that declares this class member.
	 *
	 * @return declaring class
	 */
	@DerivedProperty
	CtType<?> getDeclaringType();

	/**
	 * Returns the top level type declaring this type if an inner type or type member.
	 * If this is already a top-level type, then returns itself.
	 */
	@DerivedProperty
	<T> CtType<T> getTopLevelType();
}
