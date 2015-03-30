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

package spoon.reflect.reference;

import java.lang.annotation.Annotation;
import java.util.List;

import spoon.processing.FactoryAccessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtVisitable;
import spoon.reflect.visitor.Root;

/**
 * This is the root interface for named program element references. References
 * can point to program element built in the model or not. In the latter case,
 * introspection methods fall back on runtime reflection (
 * <code>java.lang.reflect</code>) to access the program information, as long as
 * available in the classpath.
 * 
 * @see spoon.reflect.declaration.CtElement
 */
@Root
public interface CtReference extends FactoryAccessor, CtVisitable {

	/**
	 * Gets the simple name of referenced element.
	 */
	String getSimpleName();

	/**
	 * Sets the name of referenced element.
	 */
	void setSimpleName(String simpleName);

	/**
	 * Tries to get the declaration that corresponds to the referenced element.
	 * 
	 * @return referenced element or null if element does not exist
	 */
	CtElement getDeclaration();

	/**
	 * Searches for an annotation (proxy) of the given class that annotates the
	 * current referenced element.
	 * 
	 * <p>
	 * NOTE: before using an annotation proxy, you have to make sure that all
	 * the types referenced by the annotation have been compiled and are in the
	 * classpath so that accessed values can be converted into the actual types.
	 * Otherwise, use {@link #getAnnotation(CtTypeReference)}.
	 * 
	 * @param <A>
	 *            the annotation's type
	 * @param annotationType
	 *            the annotation's class
	 * @return if found, returns a proxy for this annotation
	 */
	@Deprecated
	<A extends Annotation> A getAnnotation(Class<A> annotationType);

	/**
	 * Gets the annotation element for a given annotation type.
	 * 
	 * @param annotationType
	 *            the annotation type
	 * @return the annotation if this element is annotated by one annotation of
	 *         the given type
	 */
	@Deprecated
	<A extends Annotation> CtAnnotation<A> getAnnotation(
			CtTypeReference<A> annotationType);

	/**
	 * Returns the annotations that are present on this element.
	 */
	@Deprecated
	List<Annotation> getAnnotations();

}
