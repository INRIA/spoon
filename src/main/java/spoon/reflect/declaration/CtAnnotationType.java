/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

	/**
	 * Gets the methods of this annotation type which are necessarily {@link CtAnnotationMethod}.
	 */
	@DerivedProperty
	Set<CtAnnotationMethod<?>> getAnnotationMethods();

	/**
	 * {@inheritDoc}
	 * The method passed as parameter must be a {@link CtAnnotationMethod}.
	 */
	@Override
	<M, C extends CtType<T>> C addMethod(CtMethod<M> method);

	/**
	 * {@inheritDoc}
	 * The methods passed as parameter must be typed by {@link CtAnnotationMethod}.
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
