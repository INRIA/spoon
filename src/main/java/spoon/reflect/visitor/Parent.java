/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * This annotation defines a method that returns the parent of an element of the
 * meta model. It is used for the automatic generation of visitors of spoon
 * metamodel elements.
 */
@Target({ ElementType.METHOD })
public @interface Parent {
}
