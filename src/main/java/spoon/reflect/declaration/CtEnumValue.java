/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtRHSReceiver;
import spoon.support.UnsettableProperty;

/**
 * Corresponds to one enum value specified in an enumeration.
 * If the enum value implicitly calls a constructor (see example below),
 * it is stored in the default expression of the field as CtConstructorCall,
 *
 * <pre>
 *     class enum {
 *         ENUM_VALUE("default expression.");
 *     }
 * </pre>
 *
 * @param <T>
 * 		the type of the enum, hence equal to the type of getParent().
 */
public interface CtEnumValue<T> extends CtField<T> {
	@Override
	CtEnumValue clone();

	@Override
	@UnsettableProperty
	<U extends CtRHSReceiver<T>> U setAssignment(CtExpression<T> assignment);
}
