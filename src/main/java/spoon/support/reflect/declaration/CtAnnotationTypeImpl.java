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

import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

	private <R> CtMethod<R> createGhostMethod(CtField<R> field) {
		if (field == null) {
			return null;
		}
		final CtMethod<R> method = factory.Core().createMethod();
		method.setImplicit(true);
		method.setSimpleName(field.getSimpleName());
		method.setModifiers(field.getModifiers());
		method.setType(field.getType() == null ? null : field.getType().clone());
		for (CtAnnotation<? extends Annotation> ctAnnotation : field.getAnnotations()) {
			method.addAnnotation(ctAnnotation.clone());
		}
		for (CtComment ctComment : field.getComments()) {
			method.addComment(ctComment.clone());
		}
		method.setDocComment(field.getDocComment());
		method.setPosition(field.getPosition());
		method.setShadow(field.isShadow());
		return method;
	}

	private <R> void addGhostMethod(CtField<R> field) {
		super.addMethod(createGhostMethod(field));
	}

	@Override
	public <F, C extends CtType<T>> C addField(CtField<F> field) {
		addGhostMethod(field);
		return super.addField(field);
	}

	@Override
	public <F, C extends CtType<T>> C addField(int index, CtField<F> field) {
		addGhostMethod(field);
		return super.addField(index, field);
	}

	@Override
	public <F, C extends CtType<T>> C addFieldAtTop(CtField<F> field) {
		addGhostMethod(field);
		return super.addFieldAtTop(field);
	}

	@Override
	public <C extends CtType<T>> C setFields(List<CtField<?>> fields) {
		methods.clear();
		for (CtField<?> field : fields) {
			super.addMethod(createGhostMethod(field));
		}
		return super.setFields(fields);
	}

	@Override
	public <F> boolean removeField(CtField<F> field) {
		super.removeMethod(createGhostMethod(field));
		return super.removeField(field);
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
	public <C extends CtFormalTypeDeclarer> C setFormalTypeParameters(List<CtTypeParameterReference> formalTypeParameters) {
		throw new UnsupportedOperationException("You can't have generics in an annotation.");
	}

	@Override
	public <C extends CtFormalTypeDeclarer> C addFormalTypeParameter(CtTypeParameterReference formalTypeParameter) {
		throw new UnsupportedOperationException("You can't have generics in an annotation.");
	}

	@Override
	public boolean removeFormalTypeParameter(CtTypeParameterReference formalTypeParameter) {
		throw new UnsupportedOperationException("You can't have generics in an annotation.");
	}

	@Override
	public CtAnnotationType<T> clone() {
		return (CtAnnotationType<T>) super.clone();
	}
}
