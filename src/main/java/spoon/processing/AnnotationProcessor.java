/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
	 *
	 * @param annotation the annotation to be checked
	 * @return true, if the given annotation should be removed from the processed code.
	 *
	 * @deprecated use {@link #shouldBeConsumed(CtAnnotation)} instead
	 */
	@Deprecated
	boolean shoudBeConsumed(CtAnnotation<? extends Annotation> annotation);

	/**
	 * Returns true if this annotation should be removed from the processed code.
	 *
	 * @param annotation the annotation to be checked
	 * @return true, if the given annotation should be removed from the processed code.
	 */
	boolean shouldBeConsumed(CtAnnotation<? extends Annotation> annotation);

}
