/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.annotations;

import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.path.CtRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tells that a field is a property of the metamodel
 * For instance {@link spoon.support.reflect.declaration.CtNamedElementImpl#getSimpleName} is the property name of {@link CtNamedElement}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface MetamodelPropertyField {
	CtRole[] role();
}
