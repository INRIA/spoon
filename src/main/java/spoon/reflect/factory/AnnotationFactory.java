/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import spoon.reflect.Factory;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * The {@link CtAnnotationType} sub-factory.
 */
public class AnnotationFactory extends TypeFactory {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an annotation sub-factory.
	 * 
	 * @param factory
	 *            the parent factory
	 */
	public AnnotationFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Creates an annotation type.
	 * 
	 * @param owner
	 *            the package of the annotation type
	 * @param simpleName
	 *            the name of annotation
	 */
	public <T extends Annotation> CtAnnotationType<?> create(CtPackage owner,
			String simpleName) {
		CtAnnotationType<T> t = factory.Core().createAnnotationType();
		t.setSimpleName(simpleName);
		owner.getTypes().add(t);
		t.setParent(owner);
		return t;
	}

	/**
	 * Creates an annotation type.
	 * 
	 * @param qualifiedName
	 *            the fully qualified name of the annotation type.
	 */
	public CtAnnotationType<?> create(String qualifiedName) {
		return create(factory.Package().getOrCreate(
				getPackageName(qualifiedName)), getSimpleName(qualifiedName));
	}

	/**
	 * Gets a annotation type from its name.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Annotation> CtAnnotationType<T> getAnnotationType(String qualifiedName) {
		return (CtAnnotationType) super.get(qualifiedName);
	}

	/**
	 * Creates/updates an element's annotation value.
	 * 
	 * @param element
	 *            the program element to annotate
	 * @param annotationType
	 *            the annotation type
	 * @param annotationElementName
	 *            the annotation element name
	 * @param value
	 *            the value of the annotation element
	 * @return the created/updated annotation
	 */
	@SuppressWarnings("unchecked")
	public <A extends Annotation> CtAnnotation<A> annotate(CtElement element,
			CtTypeReference<A> annotationType,
			String annotationElementName, Object value) {
		return annotate(element,annotationType.getActualClass(),annotationElementName,value);
	}
	
	/**
	 * Creates/updates an element's annotation value.
	 * 
	 * @param element
	 *            the program element to annotate
	 * @param annotationType
	 *            the annotation type
	 * @param annotationElementName
	 *            the annotation element name
	 * @param value
	 *            the value of the annotation element
	 * @return the created/updated annotation
	 */
	@SuppressWarnings("unchecked")
	public <A extends Annotation> CtAnnotation<A> annotate(CtElement element,
			Class<A> annotationType,
			String annotationElementName, Object value) {
		CtAnnotation annotation = element.getAnnotation(factory.Type()
				.createReference(annotationType));
		if (annotation == null) {
			annotation = factory.Core().createAnnotation();
			annotation.setAnnotationType(factory.Type().createReference(
					annotationType));
			element.getAnnotations().add(annotation);
			annotation.setParent(element);
		}
		boolean isArray;

		// try with CT reflexion
		CtAnnotationType annotationtype = ((CtAnnotationType) annotation
				.getAnnotationType().getDeclaration());
		if (annotationtype != null) {
			CtField e = annotationtype.getField(annotationElementName);
			isArray = (e.getType() instanceof CtArrayTypeReference);
		} else {
			Method m = null;
			try {
				m = annotation.getAnnotationType().getActualClass().getMethod(
						annotationElementName, new Class[0]);
			} catch (Exception ex) {
				throw new RuntimeException("undefined element '"
						+ annotationElementName + "' for annotation '"
						+ annotationType.getName() + "'");
			}
			isArray = m.getReturnType().isArray();
		}
		if (isArray == ((value instanceof Collection) || value.getClass()
				.isArray())) {
			if (value.getClass().isArray()) {
				value = Arrays.asList(value);
			}
			annotation.getElementValues().put(annotationElementName, value);
		} else {
			if (isArray) {
				((List) annotation.getElementValue(annotationElementName))
						.add(value);
			} else {
				throw new RuntimeException(
						"cannot assing an array to a non-array annotation element");
			}

		}
		return annotation;
	}

	/**
	 * Adds an annotation to an element.
	 * 
	 * @param element
	 *            the program element to annotate
	 * @param annotationType
	 *            the annotation type
	 * @return the concerned annotation
	 */
	public <A extends Annotation> CtAnnotation<A> annotate(CtElement element,
			CtTypeReference<A> annotationType) {
		return annotate(element,annotationType.getActualClass());
	}
	
	/**
	 * Adds an annotation to an element.
	 * 
	 * @param element
	 *            the program element to annotate
	 * @param annotationType
	 *            the annotation type
	 * @return the concerned annotation
	 */
	public <A extends Annotation> CtAnnotation<A> annotate(CtElement element,
			Class<A> annotationType) {
		CtAnnotation<A> annotation = element.getAnnotation(factory.Type()
				.createReference(annotationType));
		if (annotation == null) {
			annotation = factory.Core().createAnnotation();
			annotation.setAnnotationType(factory.Type().createReference(
					annotationType));
			element.getAnnotations().add(annotation);
			annotation.setParent(element);
		}
		return annotation;
	}

}
