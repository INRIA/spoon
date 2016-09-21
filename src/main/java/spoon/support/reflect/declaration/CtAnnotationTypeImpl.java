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
package spoon.support.reflect.declaration;

import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The implementation for {@link spoon.reflect.declaration.CtAnnotationType}.
 *
 * @author Renaud Pawlak
 */
public class CtAnnotationTypeImpl<T extends Annotation> extends CtTypeImpl<T> implements CtAnnotationType<T> {
	private static final long serialVersionUID = 1L;

	@Override
	public void accept(CtVisitor v) {
		v.visitCtAnnotationType(this);
	}

	@Override
	public boolean isAnnotationType() {
		return true;
	}

	@Override
	public Set<CtTypeReference<?>> getSuperInterfaces() {
		return Collections.emptySet();
	}

	@Override
	public <C extends CtType<T>> C setSuperInterfaces(Set<CtTypeReference<?>> interfaces) {
		throw new UnsupportedOperationException("You can't have super interfaces in an annotation.");
	}

	@Override
	public boolean isSubtypeOf(CtTypeReference<?> type) {
		return false;
	}

	@Override
	public CtAnnotationType<T> clone() {
		return (CtAnnotationType<T>) super.clone();
	}

	@Override
	public Set<CtAnnotationMethod<?>> getAnnotationMethods() {
		Set<CtAnnotationMethod<?>> annotationsMethods = new HashSet<>();
		for (CtMethod<?> method : getMethods()) {
			annotationsMethods.add((CtAnnotationMethod<?>) method);
		}
		return annotationsMethods;
	}

	@Override
	public <M, C extends CtType<T>> C addMethod(CtMethod<M> method) {
		if (method != null && !(method instanceof CtAnnotationMethod)) {
			throw new IllegalArgumentException("The method " + method.getSignature() + " should be a " + CtAnnotationMethod.class.getName());
		}
		return super.addMethod(method);
	}
}
