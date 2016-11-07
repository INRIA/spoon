/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.reflect.declaration;

import spoon.reflect.reference.CtTypeReference;

/** A shadow element is an element that is in the Spoon model, but does not exist in the actual source code.
 * The goal of shadow elements is to simplify transformations.
 */
public interface CtShadowable {
	/**
	 * When an element isn't present in the factory (created in another factory),
	 * this element is considered as "shadow". e.g., a shadow element can be a
	 * CtType of java.lang.Class built when we call {@link CtTypeReference#getTypeDeclaration()}
	 * on a reference of java.lang.Class.
	 *
	 * @return true if the element is a shadow element, otherwise false.
	 */
	boolean isShadow();

	/**
	 * Marks an element as shadow. To know what is a shadow element, see the javadoc of
	 * {@link #isShadow()}.
	 */
	<E extends CtShadowable> E setShadow(boolean isShadow);
}
