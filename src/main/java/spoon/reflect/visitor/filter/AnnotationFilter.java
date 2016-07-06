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
