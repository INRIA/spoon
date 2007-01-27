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

package spoon.reflect.declaration;

import java.lang.annotation.Annotation;
import java.util.Map;

import spoon.reflect.reference.CtTypeReference;

/**
 * This element defines an annotation, declared on a given annotated element.
 * 
 * @param <A>
 *            type of represented annotation
 */
public interface CtAnnotation<A extends Annotation> extends CtElement {

	/**
	 * Returns the actual annotation (a dynamic proxy for this element).
	 */
	A getActualAnnotation();

	/**
	 * Returns the annotation type of this annotation.
	 * 
	 * @return a reference to the type of this annotation
	 */
	CtTypeReference<A> getAnnotationType();

	/**
	 * Searches a value for a given key
	 * 
	 * @param key
	 *            name of searched value
	 * @return the value or null if not found
	 */
	Object getElementValue(String key);

	/**
	 * Returns this annotation's elements and their values. This is returned in
	 * the form of a map that associates element names with their corresponding
	 * values.
	 * 
	 * @return this annotation's element anmes and their values, or an empty map
	 *         if there are none
	 */
	Map<String, Object> getElementValues();

	/**
	 * Sets the annotation's type.
	 * 
	 * @param type
	 *            reference to the type of this annotation
	 */
	void setAnnotationType(CtTypeReference<? extends Annotation> type);

	/**
	 * Set's this annotation's element names and their values. This is in the
	 * form of a map that associates element names with their corresponding
	 * values.
	 */
	void setElementValues(Map<String, Object> values);

}
