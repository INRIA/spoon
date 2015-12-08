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
package spoon.processing;

import spoon.Launcher;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This class defines an abstract annotation processor to be subclassed by the
 * user for defining new annotation processors including Java 8 annotations.
 */
public abstract class AbstractAnnotationProcessor<A extends Annotation, E extends CtElement> extends AbstractProcessor<E> implements AnnotationProcessor<A, E> {

	Map<String, Class<? extends A>> consumedAnnotationTypes = new TreeMap<String, Class<? extends A>>();

	Map<String, Class<? extends A>> processedAnnotationTypes = new TreeMap<String, Class<? extends A>>();

	/**
	 * Empty constructor only for all processors (invoked by Spoon).
	 */
	@SuppressWarnings("unchecked")
	public AbstractAnnotationProcessor() {
		super();
		clearProcessedElementType();

		for (Method m : getClass().getMethods()) {
			if ("process".equals(m.getName()) && (m.getParameterTypes().length == 2)) {
				Class<?> c = m.getParameterTypes()[0];
				if (inferConsumedAnnotationType() && (Annotation.class != c)) {
					addConsumedAnnotationType((Class<A>) c);
				}
				c = m.getParameterTypes()[1];
				if (CtElement.class != c) {
					addProcessedElementType((Class<E>) c);
				}
			}
		}
		if (inferConsumedAnnotationType() && processedAnnotationTypes.isEmpty()) {
			addProcessedAnnotationType((Class<? extends A>) Annotation.class);
		}
		if (processedElementTypes.isEmpty()) {
			addProcessedElementType(CtElement.class);
		}
	}

	/**
	 * Adds a consumed annotation type (to be used in subclasses constructors).
	 * A consumed annotation type is also part of the processed annotation
	 * types.
	 */
	protected final void addConsumedAnnotationType(Class<? extends A> annotationType) {
		addProcessedAnnotationType(annotationType);
		consumedAnnotationTypes.put(annotationType.getName(), annotationType);
	}

	/**
	 * Adds a processed annotation type (to be used in subclasses constructors).
	 */
	protected final void addProcessedAnnotationType(Class<? extends A> annotationType) {
		processedAnnotationTypes.put(annotationType.getName(), annotationType);
	}

	/**
	 * Removes a processed annotation type.
	 */
	protected final void removeProcessedAnnotationType(Class<? extends A> annotationType) {
		processedAnnotationTypes.remove(annotationType.getName());
	}

	/**
	 * Clears the processed annotation types.
	 */
	protected final void clearProcessedAnnotationTypes() {
		processedAnnotationTypes.clear();
	}

	/**
	 * Clears the consumed annotation types.
	 */
	protected final void clearConsumedAnnotationTypes() {
		consumedAnnotationTypes.clear();
	}

	/**
	 * Removes a processed annotation type.
	 */
	protected final void removeConsumedAnnotationType(Class<? extends A> annotationType) {
		consumedAnnotationTypes.remove(annotationType.getName());
	}

	public final Set<Class<? extends A>> getConsumedAnnotationTypes() {
		return new TreeSet<Class<? extends A>>(consumedAnnotationTypes.values());
	}

	public final Set<Class<? extends A>> getProcessedAnnotationTypes() {
		return new TreeSet<Class<? extends A>>(processedAnnotationTypes.values());
	}

	public boolean inferConsumedAnnotationType() {
		return true;
	}

	/**
	 * Returns true if the element is annotated with an annotation whose type is
	 * processed.
	 */
	@Override
	public final boolean isToBeProcessed(E element) {
		if ((element != null) && (element.getAnnotations() != null)) {
			for (CtAnnotation<? extends Annotation> a : element.getAnnotations()) {
				if (shoudBeProcessed(a)) {
					return true;
				}
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public final void process(E element) {
		for (CtAnnotation<? extends Annotation> annotation : new ArrayList<CtAnnotation<?>>(element.getAnnotations())) {
			if (shoudBeProcessed(annotation)) {
				try {
					process((A) annotation.getActualAnnotation(), element);
				} catch (Exception e) {
					Launcher.LOGGER.error(e.getMessage(), e);
				}
				if (shoudBeConsumed(annotation)) {
					element.removeAnnotation(annotation);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * Removes all annotations A on elements E.
	 */
	@Override
	public boolean shoudBeConsumed(CtAnnotation<? extends Annotation> annotation) {
		return consumedAnnotationTypes.containsKey(annotation.getAnnotationType().getQualifiedName());
	}

	private boolean shoudBeProcessed(CtAnnotation<? extends Annotation> annotation) {
		return processedAnnotationTypes.containsKey(annotation.getAnnotationType().getQualifiedName());
	}

}
