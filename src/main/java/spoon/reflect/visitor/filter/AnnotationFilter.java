/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import java.lang.annotation.Annotation;

import spoon.reflect.declaration.CtElement;

/**
 * This filter matches all the elements annotated with a given annotation type.
 */
public class AnnotationFilter<E extends CtElement> extends TypeFilter<E> {

	private Class<? extends Annotation> annotationType;

	/**
	 * Creates the filter.
	 *
	 * @param annotationType
	 * 		the annotation type which is searched
	 */
	public AnnotationFilter(Class<? extends Annotation> annotationType) {
		super(CtElement.class);
		this.annotationType = annotationType;
	}

	/**
	 * Creates a new annotation filter.
	 */
	public AnnotationFilter(Class<E> elementType,
							Class<? extends Annotation> annotationType) {
		super(elementType);
		this.annotationType = annotationType;
	}

	@Override
	public boolean matches(E element) {
		return super.matches(element) && element.getAnnotation(annotationType) != null;
	}
}
