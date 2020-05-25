/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

import java.util.List;
import java.util.Set;

/**
 * This element defines a declaration of a type parameter (aka generics).
 * For example, in class A&lt;E&gt; { ... }, the "E" is modeled as an instance of CtTypeParameter.
 */
public interface CtTypeParameter extends CtType<Object> {
	/** override the return type */
	@Override
	@DerivedProperty
	CtTypeParameterReference getReference();

	/**
	 * @return the {@link CtFormalTypeDeclarer}, which declares this {@link CtTypeParameter}
	 */
	@DerivedProperty
	CtFormalTypeDeclarer getTypeParameterDeclarer();

	// override the return type
	@Override
	CtTypeParameter clone();

	@Override
	@UnsettableProperty
	<T extends CtFormalTypeDeclarer> T setFormalCtTypeParameters(List<CtTypeParameter> formalTypeParameters);

	@Override
	@UnsettableProperty
	<C extends CtType<Object>> C setSuperInterfaces(Set<CtTypeReference<?>> interfaces);

	@Override
	@UnsettableProperty
	<S, C extends CtType<Object>> C addSuperInterface(CtTypeReference<S> interfac);

	@Override
	@UnsettableProperty
	<C extends CtType<Object>> C setTypeMembers(List<CtTypeMember> members);

	@Override
	@UnsettableProperty
	<C extends CtType<Object>> C setFields(List<CtField<?>> fields);

	@Override
	@UnsettableProperty
	<C extends CtType<Object>> C setMethods(Set<CtMethod<?>> methods);

	@Override
	@UnsettableProperty
	<M, C extends CtType<Object>> C addMethod(CtMethod<M> method);

	@Override
	@UnsettableProperty
	<C extends CtType<Object>> C setNestedTypes(Set<CtType<?>> nestedTypes);

	@Override
	@UnsettableProperty
	<N, C extends CtType<Object>> C addNestedType(CtType<N> nestedType);

	@Override
	@UnsettableProperty
	<F, C extends CtType<Object>> C addFieldAtTop(CtField<F> field);

	@Override
	@UnsettableProperty
	<T extends CtModifiable> T setModifiers(Set<ModifierKind> modifiers);
}
