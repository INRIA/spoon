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
package spoon.reflect.factory;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

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
		owner.getTypes().add(t);
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
		if (isArray == (value instanceof Collection || value.getClass().isArray())) {
			annotation.addValue(annotationElementName, value);
		} else if (isArray) {
			annotation.addValue(annotationElementName, value);
		} else {
			throw new RuntimeException("cannot assign an array to a non-array annotation element");
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
		CtAnnotation<A> annotation = element.getAnnotation(annotationType);
		if (annotation == null) {
			annotation = factory.Core().createAnnotation();
			annotation.setAnnotationType(factory.Core().clone(annotationType));
			element.addAnnotation(annotation);
		}
		return annotation;
	}

}
