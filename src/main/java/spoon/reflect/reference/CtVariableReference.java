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
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.support.DerivedProperty;

import java.util.Set;


/**
 * This interface defines a reference to a
 * {@link spoon.reflect.declaration.CtVariable} or sub-type.
 */
public interface CtVariableReference<T> extends CtReference {

	/**
	 * Gets the type of the variable.
	 */
	@PropertyGetter(role = CtRole.TYPE)
	CtTypeReference<T> getType();

	/**
	 * Sets the type of the variable.
	 */
	@PropertySetter(role = CtRole.TYPE)
	<C extends CtVariableReference<T>> C setType(CtTypeReference<T> type);

	/**
	 * Tries to get the declaration of the reference.
	 */
	@Override
	@DerivedProperty
	CtVariable<T> getDeclaration();

	/**
	 * Gets modifiers of the reference.
	 */
	Set<ModifierKind> getModifiers();

	@Override
	CtVariableReference<T> clone();
}
