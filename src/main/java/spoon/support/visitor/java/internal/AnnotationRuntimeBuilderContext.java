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
package spoon.support.visitor.java.internal;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.reference.CtTypeReference;

import java.lang.annotation.Annotation;

public class AnnotationRuntimeBuilderContext extends AbstractRuntimeBuilderContext {
	private CtAnnotation<Annotation> ctAnnotation;

	public AnnotationRuntimeBuilderContext(CtAnnotation<Annotation> ctAnnotation) {
		super(ctAnnotation);
		this.ctAnnotation = ctAnnotation;
	}

	@Override
	public void addAnnotation(CtAnnotation<Annotation> ctAnnotation) {
		this.ctAnnotation.addAnnotation(ctAnnotation);
	}

	@Override
	public void addClassReference(CtTypeReference<?> typeReference) {
		ctAnnotation.setAnnotationType((CtTypeReference<? extends Annotation>) typeReference);
	}
}
