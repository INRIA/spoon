/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import static spoon.reflect.path.CtRole.IS_SHADOW;

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
	@PropertyGetter(role = IS_SHADOW)
	boolean isShadow();

	/**
	 * Marks an element as shadow. To know what is a shadow element, see the javadoc of
	 * {@link #isShadow()}.
	 */
	@PropertySetter(role = IS_SHADOW)
	<E extends CtShadowable> E setShadow(boolean isShadow);
}
