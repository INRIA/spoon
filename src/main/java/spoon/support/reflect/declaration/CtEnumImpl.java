/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.util.SignatureBasedSortedSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static spoon.reflect.path.CtRole.VALUE;

public class CtEnumImpl<T extends Enum<?>> extends CtClassImpl<T> implements CtEnum<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = VALUE)
	private List<CtEnumValue<?>> enumValues = CtElementImpl.emptyList();

	@MetamodelPropertyField(role = VALUE)
	private CtMethod<T[]> valuesMethod;

	private CtMethod<T> valueOfMethod;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtEnum(this);
	}

	@Override
	public Set<CtMethod<?>> getAllMethods() {
		Set<CtMethod<?>> allMethods = new SignatureBasedSortedSet();
		allMethods.addAll(getMethods());
		allMethods.addAll(getFactory().Type().get(Enum.class).getMethods());
		allMethods.add(valuesMethod());
		allMethods.add(valueOfMethod());
		return allMethods;
	}

	@Override
	public boolean isSubtypeOf(CtTypeReference<?> type) {
		return getReference().isSubtypeOf(type);
	}

	@Override
	public <C extends CtEnum<T>> C addEnumValue(CtEnumValue<?> enumValue) {
		if (enumValue == null) {
			return (C) this;
		}
		if (enumValues == CtElementImpl.<CtEnumValue<?>>emptyList()) {
			enumValues = new ArrayList<>();
		}
		if (!enumValues.contains(enumValue)) {
			enumValue.setParent(this);
			getFactory().getEnvironment().getModelChangeListener().onListAdd(this, VALUE, this.enumValues, enumValue);
			enumValues.add(enumValue);
		}

		// enum value already exists.
		return (C) this;
	}

	@Override
	public boolean removeEnumValue(CtEnumValue<?> enumValue) {
		if (enumValues == CtElementImpl.<CtEnumValue<?>>emptyList()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, VALUE, enumValues, enumValues.indexOf(enumValue), enumValue);
		return enumValues.remove(enumValue);
	}

	@Override
	public CtEnumValue<?> getEnumValue(String name) {
		for (CtEnumValue<?> enumValue : enumValues) {
			if (enumValue.getSimpleName().equals(name)) {
				return enumValue;
			}
		}
		return null;
	}

	@Override
	public List<CtEnumValue<?>> getEnumValues() {
		return Collections.unmodifiableList(enumValues);
	}

	@Override
	public <C extends CtEnum<T>> C setEnumValues(List<CtEnumValue<?>> enumValues) {
		if (enumValues == null) {
			this.enumValues = emptyList();
			return (C) this;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, VALUE, this.enumValues, new ArrayList<>(enumValues));
		if (enumValues.isEmpty()) {
			this.enumValues = emptyList();
			return (C) this;
		}
		this.enumValues.clear();
		for (CtEnumValue<?> enumValue : enumValues) {
			addEnumValue(enumValue);
		}
		return (C) this;
	}

	@Override
	@DerivedProperty
	public List<CtField<?>> getFields() {
		List<CtField<?>> result = new ArrayList<>();
		result.addAll(getEnumValues());
		result.addAll(super.getFields());
		return Collections.unmodifiableList(result);
	}

	@Override
	public CtField<?> getField(String name) {
		final CtField<?> field = super.getField(name);
		if (field == null) {
			return getEnumValue(name);
		}
		return field;
	}

	@Override
	public CtEnum<T> clone() {
		return (CtEnum<T>) super.clone();
	}

	@Override
	@DerivedProperty
	public CtTypeReference<?> getSuperclass() {
		return getFactory().Type().createReference(Enum.class);
	}

	@Override
	@UnsettableProperty
	public <C extends CtType<T>> C setSuperclass(CtTypeReference<?> superClass) {
		return (C) this;
	}

	private CtMethod valuesMethod() {
		if (valuesMethod == null) {
			valuesMethod = getFactory().Core().createMethod();
			valuesMethod.setParent(this);
			valuesMethod.addModifier(ModifierKind.PUBLIC);
			valuesMethod.addModifier(ModifierKind.STATIC);
			valuesMethod.setSimpleName("values");
			valuesMethod.setImplicit(true);
			valuesMethod.setType(factory.Type().createArrayReference(getReference()));
		}
		return valuesMethod;
	}

	private CtMethod valueOfMethod() {
		if (valueOfMethod == null) {
			valueOfMethod = getFactory().Core().createMethod();
			valueOfMethod.setParent(this);
			valueOfMethod.addModifier(ModifierKind.PUBLIC);
			valueOfMethod.addModifier(ModifierKind.STATIC);
			valueOfMethod.setSimpleName("valueOf");
			valueOfMethod.setImplicit(true);
			valueOfMethod.addThrownType(
				getFactory().Type().createReference(IllegalArgumentException.class));
			valueOfMethod.setType(getReference());
			factory.Method().createParameter(valueOfMethod, factory.Type().STRING, "name");
		}
		return valueOfMethod;
	}

	@Override
	public <R> CtMethod<R> getMethod(String name, CtTypeReference<?>... parameterTypes) {
		if ("values".equals(name) && parameterTypes.length == 0) {
			return valuesMethod();
		} else if ("valueOf".equals(name) && parameterTypes.length == 1 && parameterTypes[0].equals(factory.Type().STRING)) {
			return valueOfMethod();
		} else {
			return super.getMethod(name, parameterTypes);
		}
	}

	@Override
	public <R> CtMethod<R> getMethod(CtTypeReference<R> returnType, String name, CtTypeReference<?>... parameterTypes) {
		if ("values".equals(name)
			&& parameterTypes.length == 0
			&& returnType.equals(getReference())) {
			return valuesMethod();
		} else if ("valueOf".equals(name)
			&& parameterTypes.length == 1
			&& parameterTypes[0].equals(factory.Type().STRING)
			&& returnType.equals(factory.Type().createArrayReference(getReference()))) {
			return valueOfMethod();
		} else {
			return super.getMethod(returnType, name, parameterTypes);
		}
	}

	@Override
	public boolean isClass() {
		return false;
	}

	@Override
	public boolean isEnum() {
		return true;
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
}
