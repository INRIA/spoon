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

import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CtTypeParameterImpl extends CtTypeImpl<Object> implements CtTypeParameter {
	CtTypeReference<?> superClass;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtTypeParameter(this);
	}

	@Override
	public CtTypeReference<?> getSuperclass() {
		return superClass;
	}

	@Override
	public <C extends CtType<Object>> C setSuperclass(CtTypeReference<?> superClass) {
		if (superClass != null) {
			superClass.setParent(this);
		}
		this.superClass = superClass;
		return (C) this;
	}

	@Override
	public String getQualifiedName() {
		return simpleName;
	}

	@Override
	public CtTypeParameterReference getReference() {
		return getFactory().Type().createReference(this);
	}

	@Override
	public boolean isGenerics() {
		return true;
	}

	@Override
	public CtTypeParameter clone() {
		return (CtTypeParameter) super.clone();
	}

	@Override
	@UnsettableProperty
	public <F, C extends CtType<Object>> C addFieldAtTop(CtField<F> field) {
		// unsettable property
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <F, C extends CtType<Object>> C addField(CtField<F> field) {
		// unsettable property
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <F, C extends CtType<Object>> C addField(int index, CtField<F> field) {
		// unsettable property
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <C extends CtType<Object>> C setFields(List<CtField<?>> fields) {
		// unsettable property
		return (C) this;
	}

	@Override
	@DerivedProperty
	public <F> boolean removeField(CtField<F> field) {
		// unsettable property
		return false;
	}

	@Override
	public CtField<?> getField(String name) {
		return null;
	}

	@Override
	public List<CtField<?>> getFields() {
		return Collections.emptyList();
	}

	@Override
	@UnsettableProperty
	public <N, C extends CtType<Object>> C addNestedType(CtType<N> nestedType) {
		// unsettable property
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <N> boolean removeNestedType(CtType<N> nestedType) {
		// unsettable property
		return false;
	}

	@Override
	@UnsettableProperty
	public <C extends CtType<Object>> C setNestedTypes(Set<CtType<?>> nestedTypes) {
		// unsettable property
		return (C) this;
	}

	@Override
	public <N extends CtType<?>> N getNestedType(String name) {
		return null;
	}

	@Override
	public Set<CtType<?>> getNestedTypes() {
		return Collections.emptySet();
	}

	@Override
	public CtPackage getPackage() {
		return null;
	}

	@Override
	public boolean isTopLevel() {
		return false;
	}

	@Override
	public Set<ModifierKind> getModifiers() {
		return Collections.emptySet();
	}

	@Override
	public boolean hasModifier(ModifierKind modifier) {
		return false;
	}

	@Override
	@UnsettableProperty
	public <C extends CtModifiable> C setModifiers(Set<ModifierKind> modifiers) {
		// unsettable property
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <C extends CtModifiable> C addModifier(ModifierKind modifier) {
		// unsettable property
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public boolean removeModifier(ModifierKind modifier) {
		// unsettable property
		return true;
	}

	@Override
	@UnsettableProperty
	public <C extends CtModifiable> C setVisibility(ModifierKind visibility) {
		// unsettable property
		return (C) this;
	}

	@Override
	public ModifierKind getVisibility() {
		return null;
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public boolean isAnonymous() {
		return false;
	}

	@Override
	public boolean isLocalType() {
		return false;
	}

	@Override
	public List<CtFieldReference<?>> getAllFields() {
		return Collections.emptyList();
	}

	@Override
	public List<CtFieldReference<?>> getDeclaredFields() {
		return Collections.emptyList();
	}

	@Override
	public boolean isSubtypeOf(CtTypeReference<?> type) {
		return false;
	}

	@Override
	public boolean isAssignableFrom(CtTypeReference<?> type) {
		return false;
	}

	@Override
	@UnsettableProperty
	public <M, C extends CtType<Object>> C addMethod(CtMethod<M> method) {
		// unsettable property
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <M> boolean removeMethod(CtMethod<M> method) {
		// unsettable property
		return false;
	}

	@Override
	@UnsettableProperty
	public <S, C extends CtType<Object>> C addSuperInterface(CtTypeReference<S> interfac) {
		// unsettable property
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <S> boolean removeSuperInterface(CtTypeReference<S> interfac) {
		// unsettable property
		return false;
	}

	@Override
	public <R> CtMethod<R> getMethod(CtTypeReference<R> returnType, String name, CtTypeReference<?>... parameterTypes) {
		return null;
	}

	@Override
	public <R> CtMethod<R> getMethod(String name, CtTypeReference<?>... parameterTypes) {
		return null;
	}

	@Override
	public Set<CtMethod<?>> getMethods() {
		return Collections.emptySet();
	}

	@Override
	public Set<CtMethod<?>> getMethodsAnnotatedWith(CtTypeReference<?>... annotationTypes) {
		return Collections.emptySet();
	}

	@Override
	public List<CtMethod<?>> getMethodsByName(String name) {
		return Collections.emptyList();
	}

	@Override
	public Set<CtTypeReference<?>> getSuperInterfaces() {
		return Collections.emptySet();
	}

	@Override
	@UnsettableProperty
	public <C extends CtType<Object>> C setMethods(Set<CtMethod<?>> methods) {
		// unsettable property
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <C extends CtType<Object>> C setSuperInterfaces(Set<CtTypeReference<?>> interfaces) {
		// unsettable property
		return (C) this;
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
	public Set<CtMethod<?>> getAllMethods() {
		return Collections.emptySet();
	}
}
