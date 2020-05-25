/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.visitor.GenericTypeAdapter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static spoon.reflect.path.CtRole.SUPER_TYPE;

public class CtTypeParameterImpl extends CtTypeImpl<Object> implements CtTypeParameter {
	@MetamodelPropertyField(role = SUPER_TYPE)
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
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, SUPER_TYPE, superClass, this.superClass);
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
	public CtFormalTypeDeclarer getTypeParameterDeclarer() {
		try {
			return getParent(CtFormalTypeDeclarer.class);
		} catch (ParentNotInitializedException e) {
			return null;
		}
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
	@DerivedProperty
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
	@DerivedProperty
	public Set<CtType<?>> getNestedTypes() {
		return Collections.emptySet();
	}

	@Override
	@DerivedProperty
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
	public <C extends CtModifiable> C removeModifier(ModifierKind modifier) {
		// unsettable property
		return (C) this;
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
	public boolean isSubtypeOf(CtTypeReference<?> superTypeRef) {
		if (superTypeRef instanceof CtTypeParameterReference) {
			//the type is type parameter too. Use appropriate sub type checking algorithm
			CtTypeParameter superTypeParam = (CtTypeParameter) superTypeRef.getDeclaration();
			return isSubtypeOf(getFactory().Type().createTypeAdapter(getTypeParameterDeclarer()), this, superTypeParam);
		}
		//type is normal type
		return getTypeErasure().isSubtypeOf(superTypeRef);
	}

	private static boolean isSubtypeOf(GenericTypeAdapter typeAdapter, CtTypeParameter subTypeParam, CtTypeParameter superTypeParam) {
		while (subTypeParam != null) {
			if (isSameInSameScope(subTypeParam, typeAdapter.adaptType(superTypeParam))) {
				//both type params are same
				return true;
			}
			CtTypeReference<?> superTypeOfSubTypeParam = subTypeParam.getSuperclass();
			if (superTypeOfSubTypeParam == null) {
				//there is no super type defined, so they are different type parameters
				return false;
			}
			if (superTypeOfSubTypeParam instanceof CtTypeParameterReference) {
				subTypeParam = ((CtTypeParameterReference) superTypeOfSubTypeParam).getDeclaration();
			} else {
				//the super type is not type parameter. Normal type cannot be a super type of generic parameter
				return false;
			}
		}
		return false;
	}

	/**
	 * Note: This method expects that both arguments are already adapted to the same scope
	 * @param typeParam a type param 1
	 * @param typeRef a reference to some type 2
	 * @return true if typeParam and typeRef represents same type parameter.
	 */
	private static boolean isSameInSameScope(CtTypeParameter typeParam, CtTypeReference<?> typeRef) {
		if (typeRef instanceof CtTypeParameterReference) {
			return typeParam.getSimpleName().equals(((CtTypeParameterReference) typeRef).getSimpleName());
		}
		return false;
	}

	@Override
	public CtTypeReference<?> getTypeErasure() {
		CtTypeReference<?> boundType = getBound(this);
		return boundType.getTypeErasure();
	}

	private static CtTypeReference<?> getBound(CtTypeParameter typeParam) {
		CtTypeReference<?> bound = typeParam.getSuperclass();
		if (bound == null) {
			bound = typeParam.getFactory().Type().OBJECT;
		}
		return bound;
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
	@DerivedProperty
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
	@DerivedProperty
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
	@DerivedProperty
	public Collection<CtExecutableReference<?>> getDeclaredExecutables() {
		return Collections.emptyList();
	}

	@Override
	@DerivedProperty
	public Collection<CtExecutableReference<?>> getAllExecutables() {
		return Collections.emptyList();
	}

	@Override
	@DerivedProperty
	public Set<CtMethod<?>> getAllMethods() {
		return Collections.emptySet();
	}

	@Override
	@DerivedProperty
	public List<CtTypeParameter> getFormalCtTypeParameters() {
		return emptyList();
	}

	@Override
	@UnsettableProperty
	public <C extends CtFormalTypeDeclarer> C setFormalCtTypeParameters(List<CtTypeParameter> formalTypeParameters) {
		return (C) this;
	}

	@Override
	@DerivedProperty
	public List<CtTypeMember> getTypeMembers() {
		return emptyList();
	}

	@Override
	@UnsettableProperty
	public <C extends CtType<Object>> C setTypeMembers(List<CtTypeMember> members) {
		return (C) this;
	}
}
