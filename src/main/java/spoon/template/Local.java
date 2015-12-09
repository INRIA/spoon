/**
 * Copyright (C) 2006-2015 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
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
