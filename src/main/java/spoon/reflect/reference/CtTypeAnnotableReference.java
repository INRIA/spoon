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
package spoon.reflect.reference;

import spoon.reflect.declaration.CtAnnotation;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * This reference defines an annotation, which can be declare on all types.
 *
 * <code>
 *     public class Tacos implements @TypeAnnotation ITacos extends @TypeAnnotation AbstractTacos {
 *         public @TypeAnnotation String m() throws @TypeAnnotation Exception {
 *         		Object s = new @TypeAnnotation String();
 *				return (@TypeAnnotation String) s;
 *         }
 *     }
 * </code>
 */
@Deprecated
public interface CtTypeAnnotableReference {

	/**
	 * Returns the type annotations that are present on this type.
	 *
	 * For sake of encapsulation, the returned list is unmodifiable.
	 */
	@Deprecated
	List<CtAnnotation<? extends Annotation>> getTypeAnnotations();

	/**
	 * Sets the type annotations for this type.
	 */
	@Deprecated
	<T extends CtTypeAnnotableReference> T setTypeAnnotations(List<CtAnnotation<? extends Annotation>> annotations);

	/**
	 * Adds an type annotation for this type.
	 *
	 * @param annotation
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	@Deprecated
	<T extends CtTypeAnnotableReference> T addTypeAnnotation(CtAnnotation<? extends Annotation> annotation);

	/**
	 * Removes an type annotation for this type.
	 *
	 * @param annotation
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	@Deprecated
	boolean removeTypeAnnotation(CtAnnotation<? extends Annotation> annotation);
}
