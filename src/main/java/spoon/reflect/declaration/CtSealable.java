/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;

import java.util.Collection;
import java.util.Set;

// TODO docs
// TODO return type?

public interface CtSealable {

	@PropertyGetter(role = CtRole.PERMITTED_TYPE)
	Set<CtTypeReference<?>> getPermittedTypes();

	@PropertySetter(role = CtRole.PERMITTED_TYPE)
	CtSealable setPermittedTypes(Collection<CtTypeReference<?>> permittedTypes);

	@PropertySetter(role = CtRole.PERMITTED_TYPE)
	CtSealable addPermittedType(CtTypeReference<?> type);

	@PropertySetter(role = CtRole.PERMITTED_TYPE)
	CtSealable removePermittedType(CtTypeReference<?> type);
}
