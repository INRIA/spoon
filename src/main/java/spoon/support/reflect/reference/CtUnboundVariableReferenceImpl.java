/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.reference;

import java.lang.annotation.Annotation;
import java.util.List;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

/** represents a reference to an unbound field (used when no full classpath is available */
public class CtUnboundVariableReferenceImpl<T> extends CtVariableReferenceImpl<T> implements CtUnboundVariableReference<T> {
	private static final long serialVersionUID = -932423216089690817L;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtUnboundVariableReference(this);
	}

	@Override
	public CtUnboundVariableReference<T> clone() {
		return (CtUnboundVariableReference<T>) super.clone();
	}

	@Override
	@DerivedProperty
	public List<CtAnnotation<? extends Annotation>> getAnnotations() {
		return emptyList();
	}

	@Override
	@UnsettableProperty
	public <E extends CtElement> E setAnnotations(List<CtAnnotation<? extends Annotation>> annotations) {
		return (E) this;
	}
}
