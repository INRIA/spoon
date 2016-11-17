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
	<C extends CtType<Object>> C setTypeMembers(List<CtTypeMember> members);

	@Override
	@UnsettableProperty
	<C extends CtType<Object>> C setFields(List<CtField<?>> fields);

	@Override
	@UnsettableProperty
	<C extends CtType<Object>> C setMethods(Set<CtMethod<?>> methods);

	@Override
	@UnsettableProperty
	<C extends CtType<Object>> C setNestedTypes(Set<CtType<?>> nestedTypes);
}
