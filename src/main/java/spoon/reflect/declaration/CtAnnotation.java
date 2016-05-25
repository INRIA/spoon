/**
 * Copyright (C) 2006-2015 INRIA and contributors
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

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.reference.CtTypeReference;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * This element defines an annotation, declared on a given annotated element.
 *
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
	 * classpath so that accessed values can be converted into the actual types ({@link #getElementValue(String)}).
	 */
	A getActualAnnotation();

	/**
	 * Returns the annotation type of this annotation.
	 *
	 * @return a reference to the type of this annotation
	 */
	CtTypeReference<A> getAnnotationType();

	/**
	 * Gets a value for a given key (with conversion if needed).
	 *
	 * <p>
	 * NOTE: in case of a type, the value is converted to the actual type. To
	 * access the type as a reference, use {@link #getElementValues()}, which
	 * returns a map containing the raw (unconverted) values.
	 *
	 * @param key
	 * 		name of searched value
	 * @return the value or null if not found
	 */
	@Deprecated
	<T> T getElementValue(String key);

	/**
	 * Gets a value for a given key without any conversion.
	 *
	 * @param key
	 * 		Name of searched value.
	 * @return the value expression or null if not found.
	 */
	<T extends CtExpression> T getValue(String key);

	/**
	 * Returns this annotation's elements and their values. This is returned in
	 * the form of a map that associates element names with their corresponding
	 * values.
	 *
	 * <p>
	 * Note that <code>getElementValue("key")</code> is not completely similar
	 * to <code>getElementValues().get("key")</code> since the former converts
	 * type references into the actual types.
	 * </p>
	 *
	 * <p>
	 *     Content changed: A class access (e.g., String.class) is saved as a
	 *     CtFieldAccess and no more a CtFieldReference.
	 * </p>
	 *
	 * @return this annotation's element names and their values, or an empty map
	 * if there are none
	 * @see #getValues()
	 */
	@Deprecated
	Map<String, Object> getElementValues();

	/**
	 * Returns this annotation's elements and their values. This is returned in
	 * the form of a map that associates element names with their corresponding
	 * values.
	 *
	 * @return this annotation's element names and their values, or an empty map
	 * if there are none
	 */
	Map<String, CtExpression> getValues();

	/**
	 * Sets the annotation's type.
	 *
	 * @param type
	 * 		reference to the type of this annotation
	 */
	<T extends CtAnnotation<A>> T setAnnotationType(CtTypeReference<? extends Annotation> type);

	/**
	 * Set's this annotation's element names and their values. This is in the
	 * form of a map that associates element names with their corresponding
	 * values. Note that type values are stored as
	 * {@link spoon.reflect.reference.CtTypeReference}.
	 */
	<T extends CtAnnotation<A>> T setElementValues(Map<String, Object> values);

	/**
	 * Set's this annotation's element names and their values. This is in the
	 * form of a map that associates element names with their corresponding
	 * values.
	 */
	<T extends CtAnnotation<A>> T setValues(Map<String, CtExpression> values);

	/**
	 * Returns the element which is annotated by this annotation.
	 *
	 * @return annotated {@link spoon.reflect.declaration.CtElement}
	 */
	CtElement getAnnotatedElement();

	/**
	 * Returns the type of the element which is annotated by this annotation.
	 *
	 * @return {@link spoon.reflect.declaration.CtAnnotatedElementType}
	 */
	CtAnnotatedElementType getAnnotatedElementType();

	/**
	 * Adds a new key-value pair for this annotation
	 */
	<T extends CtAnnotation<A>> T addValue(String elementName, Object value);

	/**
	 * Adds a new key-literal pair for this annotation.
	 */
	<T extends CtAnnotation<A>> T addValue(String elementName, CtLiteral<?> value);

	/**
	 * Adds a new key-array pair for this annotation.
	 */
	<T extends CtAnnotation<A>> T addValue(String elementName, CtNewArray<? extends CtExpression> value);

	/**
	 * Adds a new key-field access pair for this annotation.
	 */
	<T extends CtAnnotation<A>> T addValue(String elementName, CtFieldAccess<?> value);

	/**
	 * Adds a new key-annotation pair for this annotation.
	 */
	<T extends CtAnnotation<A>> T addValue(String elementName, CtAnnotation<?> value);

	@Override
	CtAnnotation<A> clone();
}
