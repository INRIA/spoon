/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support;

import spoon.reflect.declaration.CtType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tells that a metamodel property is derived, ie computed from the value of another property.
 *
 * For instance {@link CtType#getFields()}  is derived from {@link CtType#getTypeMembers()}
 *
 * It can be put on getter and setters.
 *
 * Contracts:
 * - A setter with @DerivedProperty only triggers one single model intercession event,
 *   on the element primarily responsible for handling the state from which this dervied property is computed.
 * - A getter with @DerivedProperty is never called in CtScanner and derived classes (clone, replace)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface DerivedProperty {
}
