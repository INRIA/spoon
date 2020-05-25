/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.factory;

import spoon.SpoonException;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Method;

/**
 * The {@link CtAnnotationType} sub-factory.
 */
public class AnnotationFactory extends TypeFactory {

	/**
	 * Creates an annotation sub-factory.
	 *
	 * @param factory
	 * 		the parent factory
	 */
	public AnnotationFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Creates an annotation type.
	 *
	 * @param owner
	 * 		the package of the annotation type
	 * @param simpleName
	 * 		the name of annotation
	 */
	public <T extends Annotation> CtAnnotationType<?> create(CtPackage owner, String simpleName) {
		CtAnnotationType<T> t = factory.Core().createAnnotationType();
		t.setSimpleName(simpleName);
		owner.addType(t);
		return t;
	}

	/**
	 * Creates an annotation type.
	 *
	 * @param qualifiedName
	 * 		the fully qualified name of the annotation type.
	 */
	public CtAnnotationType<?> create(String qualifiedName) {
		return create(factory.Package().getOrCreate(getPackageName(qualifiedName)), getSimpleName(qualifiedName));
	}

	/**
	 * Gets a annotation type from its name.
	 */
	public <T extends Annotation> CtType<T> getAnnotationType(String qualifiedName) {
		return get(qualifiedName);
	}

	/**
	 * Creates/updates an element's annotation value.
	 *
	 * @param element
	 * 		the program element to annotate
	 * @param annotationType
	 * 		the annotation type
	 * @param annotationElementName
	 * 		the annotation element name
	 * @param value
	 * 		the value of the annotation element
	 * @return the created/updated annotation
	 */
	public <A extends Annotation> CtAnnotation<A> annotate(CtElement element, Class<A> annotationType,
			String annotationElementName, Object value) {
		return annotate(element, factory.Type().createReference(annotationType), annotationElementName, value);
	}

	/**
	 * Creates/updates an element's annotation value.
	 *
	 * @param element
	 * 		the program element to annotate
	 * @param annotationType
	 * 		the annotation type
	 * @param annotationElementName
	 * 		the annotation element name
	 * @param value
	 * 		the value of the annotation element
	 * @return the created/updated annotation
	 */
	public <A extends Annotation> CtAnnotation<A> annotate(CtElement element, CtTypeReference<A> annotationType, String annotationElementName, Object value) {
		final CtAnnotation<A> annotation = annotate(element, annotationType);
		boolean isArray;
		// try with CT reflection
		CtAnnotationType<A> ctAnnotationType = ((CtAnnotationType<A>) annotation.getAnnotationType().getDeclaration());
		boolean hasAlreadyValue = annotation.getValues().containsKey(annotationElementName);
		if (ctAnnotationType != null) {
			CtMethod<?> e = ctAnnotationType.getMethod(annotationElementName);
			isArray = (e.getType() instanceof CtArrayTypeReference);
		} else {
			Method m;
			try {
				m = annotation.getAnnotationType().getActualClass().getMethod(annotationElementName, new Class[0]);
			} catch (Exception ex) {
				annotation.addValue(annotationElementName, value);
				return annotation;
			}
			isArray = m.getReturnType().isArray();
		}
		if (isArray || !hasAlreadyValue) {
			annotation.addValue(annotationElementName, value);
		} else {
			throw new SpoonException("cannot assign an array to a non-array annotation element");
		}
		return annotation;
	}

	/**
	 * Adds an annotation to an element.
	 *
	 * @param element
	 * 		the program element to annotate
	 * @param annotationType
	 * 		the annotation type
	 * @return the concerned annotation
	 */
	public <A extends Annotation> CtAnnotation<A> annotate(CtElement element, Class<A> annotationType) {
		return annotate(element, factory.Type().createReference(annotationType));
	}

	/**
	 * Adds an annotation to an element.
	 *
	 * @param element
	 * 		the program element to annotate
	 * @param annotationType
	 * 		the annotation type
	 * @return the concerned annotation
	 */
	public <A extends Annotation> CtAnnotation<A> annotate(CtElement element, CtTypeReference<A> annotationType) {
		CtAnnotationType<A> ctAnnotationType = ((CtAnnotationType<A>) annotationType.getDeclaration());
		boolean isRepeatable = false;
		if (ctAnnotationType != null) {
			isRepeatable = (ctAnnotationType.getAnnotation(factory.Type().createReference(Repeatable.class)) != null);
		}
		CtAnnotation<A> annotation = element.getAnnotation(annotationType);
		if (annotation == null || isRepeatable) {
			annotation = factory.Core().createAnnotation();
			annotation.setAnnotationType(factory.Core().clone(annotationType));
			element.addAnnotation(annotation);
		}
		return annotation;
	}

}
