/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import java.util.List;

import static spoon.reflect.path.CtRole.TYPE_PARAMETER;

/**
 * This abstract element defines a declaration that accepts formal type
 * parameters (aka generics), such as a CtType (<code>class A&lt;E&gt;</code>), CtMethod or CtConstructor.
 */
public interface CtFormalTypeDeclarer extends CtTypeMember {

	/**
	 * Returns the formal type parameters of this generic element.
	 */
	@PropertyGetter(role = TYPE_PARAMETER)
	List<CtTypeParameter> getFormalCtTypeParameters();

	/**
	 * Sets the type parameters of this generic element.
	 */
	@PropertySetter(role = TYPE_PARAMETER)
	<T extends CtFormalTypeDeclarer> T setFormalCtTypeParameters(List<CtTypeParameter> formalTypeParameters);

	/**
	 * Add a type parameter to this generic element.
	 */
	@PropertySetter(role = TYPE_PARAMETER)
	<T extends CtFormalTypeDeclarer> T addFormalCtTypeParameter(CtTypeParameter formalTypeParameter);

	/**
	 * Removes a type parameters from this generic element.
	 */
	@PropertySetter(role = TYPE_PARAMETER)
	boolean removeFormalCtTypeParameter(CtTypeParameter formalTypeParameter);
}
