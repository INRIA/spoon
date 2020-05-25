/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * This element defines an annotation type.
 */
public interface CtAnnotationType<T extends Annotation> extends CtType<T> {

	@DerivedProperty
	Set<CtAnnotationMethod<?>> getAnnotationMethods();

	/**
	 * {@inheritDoc}
	 */
	@Override
	<M, C extends CtType<T>> C addMethod(CtMethod<M> method);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<C extends CtType<T>> C setMethods(Set<CtMethod<?>> methods);

	@Override
	CtAnnotationType<T> clone();

	@Override
	@UnsettableProperty
	<T extends CtFormalTypeDeclarer> T setFormalCtTypeParameters(List<CtTypeParameter> formalTypeParameters);

	@Override
	@UnsettableProperty
	<C extends CtType<T>> C setSuperInterfaces(Set<CtTypeReference<?>> interfaces);

	@Override
	@UnsettableProperty
	<C extends CtType<T>> C setSuperclass(CtTypeReference<?> superClass);

}
