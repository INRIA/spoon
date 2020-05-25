/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.template;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * This annotation should be placed on templates' members to indicate that they
 * are local elements of the template class. As such, they are not used either
 * for matching or generating code.
 *
 * @see spoon.template.Substitution#insertAll(spoon.reflect.declaration.CtType, Template)
 */
@Target({
		ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD,
		ElementType.TYPE
})
public @interface Local {
}
