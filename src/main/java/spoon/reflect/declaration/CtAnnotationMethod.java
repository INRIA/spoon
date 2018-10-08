/**
 * Copyright (C) 2006-2018 INRIA and contributors
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


import java.util.List;
import java.util.Set;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.UnsettableProperty;

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
	CtAnnotationMethod<T> setDefaultExpression(CtExpression<T> assignedExpression);

	@Override
	CtAnnotationMethod<T> clone();

	@Override
	@UnsettableProperty
	CtAnnotationMethod<T> setBody(CtStatement body);

	@Override
	@UnsettableProperty
	CtAnnotationMethod<T> setThrownTypes(Set<CtTypeReference<? extends Throwable>> thrownTypes);

	@Override
	@UnsettableProperty
	CtAnnotationMethod<T> setFormalCtTypeParameters(List<CtTypeParameter> formalTypeParameters);

	@Override
	@UnsettableProperty
	CtAnnotationMethod<T> setParameters(List<CtParameter<?>> parameters);
}
