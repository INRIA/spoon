/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

public interface CtRecordComponent<T> extends CtTypedElement<T>, CtNamedElement {

	/**
	 * Converts the component to an implicit method.
	 * @return the method
	 */
	CtMethod<?> toMethod();

	/**
	 * Converts the component to an implicit field.
	 * @return  the field
	 */
	CtField<?> toField();

	@Override
	CtRecordComponent<T> clone();
}
