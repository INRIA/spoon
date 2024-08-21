/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;

import java.util.Collection;
import java.util.Set;

/**
 * This interface represents any type that can be sealed.
 * See JLS 8.1.1.2
 */
public interface CtSealable {

	/**
	 * Returns the permitted types for this type.
	 *
	 * @return an unmodifiable view of the permitted types.
	 */
	@PropertyGetter(role = CtRole.PERMITTED_TYPE)
	Set<CtTypeReference<?>> getPermittedTypes();

	/**
	 * Sets the permitted types for this type.
	 * Calling this method does not change the state of the {@link ModifierKind#SEALED} for this type.
	 * The previously permitted types will be removed.
	 *
	 * @param permittedTypes the permitted types to set.
	 * @return this.
	 */
	@PropertySetter(role = CtRole.PERMITTED_TYPE)
	CtSealable setPermittedTypes(Collection<CtTypeReference<?>> permittedTypes);

	/**
	 * Adds a permitted type to this type.
	 * Calling this method does not change the state of the {@link ModifierKind#SEALED} for this type.
	 *
	 * @param type the type to add as permitted type.
	 * @return this.
	 */
	@PropertySetter(role = CtRole.PERMITTED_TYPE)
	CtSealable addPermittedType(CtTypeReference<?> type);

	/**
	 * Adds a permitted type to this type.
	 * Calling this method does not change the state of the {@link ModifierKind#SEALED} for this type.
	 *
	 * @param type the type to remove from this type's permitted types.
	 * @return this.
	 */
	@PropertySetter(role = CtRole.PERMITTED_TYPE)
	CtSealable removePermittedType(CtTypeReference<?> type);
}
