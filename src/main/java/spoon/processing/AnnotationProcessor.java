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
package spoon.processing;

import java.lang.annotation.Annotation;
import java.util.Set;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;

/**
 * This interface defines an annotation processor. An annotation processor is
 * triggered by Spoon when a visited element is annotated with one of the
 * processed or consumed annotations. To define a new annotation processor, the
 * user should subclass {@link spoon.processing.AbstractAnnotationProcessor},
 * the abstract default implementation of this interface.
 */
public interface AnnotationProcessor<A extends Annotation, E extends CtElement>
		extends Processor<E> {

	/**
	 * Do the annotation processing job for a given annotation.
	 *
	 * @param annotation
	 * 		the annotation to process
	 * @param element
	 * 		the element that holds the processed annotations
	 */
	void process(A annotation, E element);

	/**
	 * Gets the annotations processed by this annotation processor, that is to
	 * say the annotation types that trigger the
	 * {@link #process(Annotation, CtElement)} method when visiting a program
	 * element. The processed annotation types includes all the consumed
	 * annotation types.
	 *
	 * @return the annotation classes
	 */
	Set<Class<? extends A>> getProcessedAnnotationTypes();

	/**
	 * Gets the annotations types consumed by this processor. A consumed
	 * annotation is a special kind of processed annotation (see
	 * {@link #getProcessedAnnotationTypes()} that is automatically removed from
	 * the program once the associated processor has finished its job.
	 *
	 * @return the annotation classes
	 */
	Set<Class<? extends A>> getConsumedAnnotationTypes();

	/**
	 * Returns true (default) if the processor automatically infers the consumed
	 * annotation type to the <code>A</code> actual type.
	 */
	boolean inferConsumedAnnotationType();

	/**
	 * Returns true if this annotation should be removed from the processed code.
	 */
	boolean shoudBeConsumed(CtAnnotation<? extends Annotation> annotation);

}
