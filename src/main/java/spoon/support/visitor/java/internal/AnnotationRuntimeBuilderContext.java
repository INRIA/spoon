/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor.java.internal;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;

import java.lang.annotation.Annotation;
import java.util.Objects;

public class AnnotationRuntimeBuilderContext extends AbstractRuntimeBuilderContext {
	private final CtAnnotation<Annotation> ctAnnotation;

	public AnnotationRuntimeBuilderContext(CtAnnotation<Annotation> ctAnnotation) {
		super(ctAnnotation);
		this.ctAnnotation = ctAnnotation;
	}

	@Override
	public void addAnnotation(CtAnnotation<Annotation> ctAnnotation) {
		this.ctAnnotation.addAnnotation(ctAnnotation);
	}

	@Override
	public void addTypeReference(CtRole role, CtTypeReference<?> typeReference) {
        if (Objects.requireNonNull(role) == CtRole.ANNOTATION_TYPE) {
            ctAnnotation.setAnnotationType((CtTypeReference<? extends Annotation>) typeReference);
            ctAnnotation.setType((CtTypeReference<Annotation>) typeReference);
            return;
        }
		super.addTypeReference(role, typeReference);
	}

	public CtAnnotation<Annotation> getCtAnnotation() {
		return this.ctAnnotation;
	}
}
