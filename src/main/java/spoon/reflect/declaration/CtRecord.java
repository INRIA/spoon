/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import java.util.Set;

public interface CtRecord<T> extends CtClass<T> {

	<C> CtRecord<T>  addRecordComponent(CtRecordComponent<C> component);

	<C> CtRecord<T>  removeRecordComponent(CtRecordComponent<C> component);

	Set<CtRecordComponent<?>> getRecordComponents();

	CtRecord<T> setRecordComponents(Set<CtRecordComponent<?>> components);
}
