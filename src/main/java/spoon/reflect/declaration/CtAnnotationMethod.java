/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.support.UnsettableProperty;

import java.util.List;
import java.util.Set;

import static spoon.reflect.path.CtRole.DEFAULT_EXPRESSION;

/**
 * This element defines an annotation method declared in an annotation type.
 */
public interface CtAnnotationMethod<T> extends CtMethod<T> {
	/**
	 * Gets the default expression assigned to the annotation method.
	 */
	@PropertyGetter(role = DEFAULT_EXPRESSION)
	CtExpression<T> getDefaultExpression();

	/**
	 * Sets the default expression assigned to the annotation method.
	 */
	@PropertySetter(role = DEFAULT_EXPRESSION)
	<C extends CtAnnotationMethod<T>> C setDefaultExpression(CtExpression<T> assignedExpression);

	@Override
	CtAnnotationMethod<T> clone();

	@Override
	@UnsettableProperty
	<T1 extends CtBodyHolder> T1 setBody(CtStatement body);

	@Override
	@UnsettableProperty
	<T1 extends CtExecutable<T>> T1 setThrownTypes(Set<CtTypeReference<? extends Throwable>> thrownTypes);

	@Override
	@UnsettableProperty
	<T extends CtFormalTypeDeclarer> T setFormalCtTypeParameters(List<CtTypeParameter> formalTypeParameters);

	@Override
	@UnsettableProperty
	<T1 extends CtExecutable<T>> T1 setParameters(List<CtParameter<?>> parameters);
}
