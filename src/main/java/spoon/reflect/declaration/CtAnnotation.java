/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.support.UnsettableProperty;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import static spoon.reflect.path.CtRole.ANNOTATION_TYPE;
import static spoon.reflect.path.CtRole.VALUE;

/**
 * This element represents an annotation on an element.
 *
 * <pre>
 *     // statement annotated by annotation @SuppressWarnings
 *     &#64;SuppressWarnings("unchecked")
 *     java.util.List&lt;?&gt; x = new java.util.ArrayList&lt;&gt;()
 * </pre>
 * @param <A>
 * 		type of represented annotation
 */
public interface CtAnnotation<A extends Annotation> extends CtExpression<A>, CtShadowable {

	/**
	 * Returns the actual annotation (a dynamic proxy for this element).
	 *
	 * <p>
	 * NOTE: before using an annotation proxy, you have to make sure that all
	 * the types referenced by the annotation have been compiled and are in the
	 * classpath so that accessed values can be converted into the actual types.
	 */
	@DerivedProperty
	A getActualAnnotation();

	/**
	 * Returns the annotation type of this annotation.
	 *
	 * @return a reference to the type of this annotation
	 */
	@PropertyGetter(role = ANNOTATION_TYPE)
	CtTypeReference<A> getAnnotationType();

	/**
	 * Gets a value, as a CtExpression, for a given key without any conversion.
	 *
	 * If you need the actual value (eg an integer and not a literal, see {@link #getValueAsObject(String)} and similar methods.
	 *
	 * Note that this value type does not necessarily corresponds to the annotation
	 * type member. For example, in case the annotation type expect an array of Object,
	 * and a single value is given, Spoon will return only the object without the CtNewArray.
	 * If you want to get a type closer to the annotation type one, see {@link #getWrappedValue(String)}.
	 *
	 * @param key
	 * 		Name of searched value.
	 * @return the value expression or null if not found.
	 */
	@PropertyGetter(role = VALUE)
	<T extends CtExpression> T getValue(String key);

	/** Returns the actual value of an annotation property */
	@DerivedProperty
	Object getValueAsObject(String key);

	/** Returns the actual value of an annotation property, as an integer (utility method) */
	@DerivedProperty
	int getValueAsInt(String key);

	/** Returns the actual value of an annotation property, as a String (utility method) */
	@DerivedProperty
	String getValueAsString(String key);

	/**
	 * Gets a value for a given key and try to fix its type based on the
	 * annotation type. For example, if the annotation type member expects an array of String,
	 * and it can be resolved, this method will return a CtNewArray instead of a CtLiteral.
	 *
	 * Warning: the returned element might be detached from the model
	 *
	 * @param key
	 * 		Name of searched value.
	 * @return the value expression or null if not found.
	 */
	@DerivedProperty
	@PropertyGetter(role = VALUE)
	<T extends CtExpression> T getWrappedValue(String key);

	/**
	 * Returns this annotation's elements and their values. This is returned in
	 * the form of a map that associates element names with their corresponding
	 * values. If you iterate over the map with entrySet(), the iteration order
	 * complies with the order of annotation values in the source code.
	 *
	 * @return this annotation's element names and their values, or an empty map
	 * if there are none
	 */
	@PropertyGetter(role = VALUE)
	Map<String, CtExpression> getValues();

	/** Get all values of {@link #getValues()}, plus the default ones defined in the annotation type. */
	@DerivedProperty
	Map<String, CtExpression> getAllValues();

	/**
	 * Sets the annotation's type.
	 *
	 * @param type
	 * 		reference to the type of this annotation
	 */
	@PropertySetter(role = ANNOTATION_TYPE)
	<T extends CtAnnotation<A>> T setAnnotationType(CtTypeReference<? extends Annotation> type);

	/**
	 * Set's this annotation's element names and their values. This is in the
	 * form of a map that associates element names with their corresponding
	 * values. Note that type values are stored as
	 * {@link spoon.reflect.reference.CtTypeReference}.
	 */
	@PropertySetter(role = VALUE)
	<T extends CtAnnotation<A>> T setElementValues(Map<String, Object> values);

	/**
	 * Set's this annotation's element names and their values. This is in the
	 * form of a map that associates element names with their corresponding
	 * values.
	 */
	@PropertySetter(role = VALUE)
	<T extends CtAnnotation<A>> T setValues(Map<String, CtExpression> values);

	/**
	 * Returns the element which is annotated by this annotation.
	 *
	 * @return annotated {@link spoon.reflect.declaration.CtElement}
	 */
	@DerivedProperty // the annotation is contained by the element not the other way around
	CtElement getAnnotatedElement();

	/**
	 * Returns the type of the element which is annotated by this annotation.
	 *
	 * @return {@link spoon.reflect.declaration.CtAnnotatedElementType}
	 */
	@DerivedProperty
	CtAnnotatedElementType getAnnotatedElementType();

	/**
	 * Adds a new key-value pair for this annotation
	 */
	@PropertySetter(role = VALUE)
	<T extends CtAnnotation<A>> T addValue(String elementName, Object value);

	/**
	 * Adds a new key-literal pair for this annotation.
	 */
	@PropertySetter(role = VALUE)
	<T extends CtAnnotation<A>> T addValue(String elementName, CtLiteral<?> value);

	/**
	 * Adds a new key-array pair for this annotation.
	 */
	@PropertySetter(role = VALUE)
	<T extends CtAnnotation<A>> T addValue(String elementName, CtNewArray<? extends CtExpression> value);

	/**
	 * Adds a new key-field access pair for this annotation.
	 */
	@PropertySetter(role = VALUE)
	<T extends CtAnnotation<A>> T addValue(String elementName, CtFieldAccess<?> value);

	/**
	 * Adds a new key-annotation pair for this annotation.
	 */
	@PropertySetter(role = VALUE)
	<T extends CtAnnotation<A>> T addValue(String elementName, CtAnnotation<?> value);

	@Override
	CtAnnotation<A> clone();

	@Override
	@UnsettableProperty
	<C extends CtExpression<A>> C setTypeCasts(List<CtTypeReference<?>> types);

	static CtAnnotatedElementType getAnnotatedElementTypeForCtElement(CtElement element) {
		if (element == null) {
			return null;
		}

		if (element instanceof CtMethod) {
			return CtAnnotatedElementType.METHOD;
		}
		if (element instanceof CtAnnotation || element instanceof CtAnnotationType) {
			return CtAnnotatedElementType.ANNOTATION_TYPE;
		}
		if (element instanceof CtType) {
			return CtAnnotatedElementType.TYPE;
		}
		if (element instanceof CtField) {
			return CtAnnotatedElementType.FIELD;
		}
		if (element instanceof CtConstructor) {
			return CtAnnotatedElementType.CONSTRUCTOR;
		}
		if (element instanceof CtParameter) {
			return CtAnnotatedElementType.PARAMETER;
		}
		if (element instanceof CtLocalVariable) {
			return CtAnnotatedElementType.LOCAL_VARIABLE;
		}
		if (element instanceof CtPackage) {
			return CtAnnotatedElementType.PACKAGE;
		}
		if (element instanceof CtTypeParameterReference) {
			return CtAnnotatedElementType.TYPE_PARAMETER;
		}
		if (element instanceof CtTypeReference) {
			return CtAnnotatedElementType.TYPE_USE;
		}
		return null;
	}
}
