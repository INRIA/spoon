/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.reference.CtTypeReference;
import spoon.support.UnsettableProperty;

import java.util.List;
import java.util.Set;

/**
 * This element defines an anonymous executable block declaration in a class.
 *
 * @see spoon.reflect.declaration.CtClass
 */
public interface CtAnonymousExecutable extends CtExecutable<Void>, CtTypeMember {
	@Override
	CtAnonymousExecutable clone();

	@Override
	@UnsettableProperty
	<C extends CtNamedElement> C setSimpleName(String simpleName);

	@Override
	@UnsettableProperty
	<T extends CtExecutable<Void>> T setThrownTypes(Set<CtTypeReference<? extends Throwable>> thrownTypes);

	@Override
	@UnsettableProperty
	<T extends CtExecutable<Void>> T setParameters(List<CtParameter<?>> parameters);

	@Override
	@UnsettableProperty
	<C extends CtTypedElement> C setType(CtTypeReference<Void> type);

	@Override
	@UnsettableProperty
	<T extends CtExecutable<Void>> T addParameter(CtParameter<?> parameter);

	@Override
	@UnsettableProperty
	<T extends CtExecutable<Void>> T addThrownType(CtTypeReference<? extends Throwable> throwType);

}
