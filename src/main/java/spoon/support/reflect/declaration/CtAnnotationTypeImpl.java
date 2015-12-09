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
package spoon.support.reflect.declaration;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtGenericElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

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
	public Collection<CtExecutableReference<?>> getDeclaredExecutables() {
		return Collections.emptyList();
	}

	@Override
	public Collection<CtExecutableReference<?>> getAllExecutables() {
		return Collections.emptyList();
	}

	@Override
	public <C extends CtType<T>> C setMethods(Set<CtMethod<?>> methods) {
		throw new UnsupportedOperationException("You can't have methods in an annotation.");
	}

	@Override
	public <M, C extends CtType<T>> C addMethod(CtMethod<M> method) {
		throw new UnsupportedOperationException("You can't have methods in an annotation.");
	}

	@Override
	public <M> boolean removeMethod(CtMethod<M> method) {
		throw new UnsupportedOperationException("You can't have methods in an annotation.");
	}

	@Override
	public <C extends CtGenericElement> C setFormalTypeParameters(List<CtTypeReference<?>> formalTypeParameters) {
		throw new UnsupportedOperationException("You can't have generics in an annotation.");
	}

	@Override
	public <C extends CtGenericElement> C addFormalTypeParameter(CtTypeReference<?> formalTypeParameter) {
		throw new UnsupportedOperationException("You can't have generics in an annotation.");
	}

	@Override
	public boolean removeFormalTypeParameter(CtTypeReference<?> formalTypeParameter) {
		throw new UnsupportedOperationException("You can't have generics in an annotation.");
	}
}
