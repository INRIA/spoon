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
/**
 * This element represents a record declaration.
 *
 * Example:
 * <pre>
 *    record Point(int x, int y) {
 *    }
 * </pre>
 */
public interface CtRecord extends CtClass<Object> {

	@PropertySetter(role = CtRole.RECORD_COMPONENT)
	CtRecord  addRecordComponent(CtRecordComponent component);
	@PropertySetter(role = CtRole.RECORD_COMPONENT)
	CtRecord  removeRecordComponent(CtRecordComponent component);

	@PropertyGetter(role = CtRole.RECORD_COMPONENT)
	Set<CtRecordComponent> getRecordComponents();
	@PropertySetter(role = CtRole.RECORD_COMPONENT)
	CtRecord setRecordComponents(Set<CtRecordComponent> components);

	@Override
	CtRecord clone();

	@Override
	@UnsettableProperty
	<C extends CtType<Object>> C setSuperclass(CtTypeReference<?> superClass);
}
