/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import java.util.Set;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.UnsettableProperty;

public interface CtRecord<T> extends CtClass<T> {

	@PropertySetter(role = CtRole.RECORD_COMPONENT)
	<C> CtRecord<T>  addRecordComponent(CtRecordComponent<C> component);
	@PropertySetter(role = CtRole.RECORD_COMPONENT)
	<C> CtRecord<T>  removeRecordComponent(CtRecordComponent<C> component);

	@PropertyGetter(role = CtRole.RECORD_COMPONENT)
	Set<CtRecordComponent<?>> getRecordComponents();
	@PropertySetter(role = CtRole.RECORD_COMPONENT)
	CtRecord<T> setRecordComponents(Set<CtRecordComponent<?>> components);

	@Override
	CtRecord<T> clone();

	@Override
	@UnsettableProperty
	<C extends CtType<T>> C setSuperclass(CtTypeReference<?> superClass);
}
