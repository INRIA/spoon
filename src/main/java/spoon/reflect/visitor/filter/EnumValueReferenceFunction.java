/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtEnumValue;

/**
 * This query expects a {@link CtEnumValue} as input
 * and returns all {@link spoon.reflect.reference.CtFieldReference}s, which refer to this input.
 * <br>
 * Usage:<br>
 * <pre> {@code
 * CtEnumValue ev = ...;
 * ev
 *   .map(new EnumValueReferenceFunction())
 *   .forEach((CtEnumValueReference ref)->...process references...);
 * }
 * </pre>
 */
public class EnumValueReferenceFunction extends FieldReferenceFunction {
	/**
	 * Creates a new {@link EnumValueReferenceFunction}.
	 */
	public EnumValueReferenceFunction() {
		super();
	}

	/**
	 * Creates a new {@link EnumValueReferenceFunction}.
	 *
	 * @param element The enum value to find references to
	 */
	public EnumValueReferenceFunction(CtEnumValue<?> element) {
		super(element);
	}
}
