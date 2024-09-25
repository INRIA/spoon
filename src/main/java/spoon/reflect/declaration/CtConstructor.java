/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import static spoon.reflect.path.CtRole.NAME;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.UnsettableProperty;

/**
 * This element defines a constructor declaration.
 */
public interface CtConstructor<T> extends CtExecutable<T>, CtFormalTypeDeclarer, CtShadowable {

	/**
	 * Always returns "&lt;init&gt;".
	 */
	@Override
	@PropertyGetter(role = NAME)
	String getSimpleName();

	@Override
	CtConstructor<T> clone();

	@Override
	@UnsettableProperty
	<C extends CtTypedElement> C setType(CtTypeReference type);

	@Override
	@UnsettableProperty
	<C extends CtNamedElement> C setSimpleName(String simpleName);
	/**
	 * Checks if the constructor is a compact constructor. Only records have compact constructors.
	 * @return true if the constructor is a compact constructor.
	 */
	@PropertyGetter(role = CtRole.COMPACT_CONSTRUCTOR)
	boolean isCompactConstructor();
	/**
	 * Marks the constructor as a compact constructor. Only records have compact constructors.
	 * @param compactConstructor   true if the constructor is a compact constructor, false otherwise
	 */
	@PropertySetter(role = CtRole.COMPACT_CONSTRUCTOR)
	void setCompactConstructor(boolean compactConstructor);

}
