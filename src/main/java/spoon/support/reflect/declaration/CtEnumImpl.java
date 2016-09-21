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

import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CtEnumImpl<T extends Enum<?>> extends CtClassImpl<T> implements CtEnum<T> {
	private static final long serialVersionUID = 1L;

	private List<CtEnumValue<?>> enumValues = CtElementImpl.emptyList();

	private CtMethod<T[]> valuesMethod;

	private CtMethod<T> valueOfMethod;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtEnum(this);
	}

	@Override
	public Set<CtMethod<?>> getAllMethods() {
		Set<CtMethod<?>> allMethods = new HashSet<>(getMethods());
		allMethods.add(valuesMethod());
		allMethods.add(valueOfMethod());
		return allMethods;
	}

	@Override
	public boolean isSubtypeOf(CtTypeReference<?> type) {
		for (CtTypeReference<?> ref : getSuperInterfaces()) {
			if (ref.isSubtypeOf(type)) {
				return true;
			}
		}
		return getSuperclass().isSubtypeOf(type);
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
			enumValues.add(enumValue);
		}

		// enum value already exists.
		return (C) this;
	}

	@Override
	public boolean removeEnumValue(CtEnumValue<?> enumValue) {
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
		if (enumValues == null || enumValues.isEmpty()) {
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
	public List<CtField<?>> getFields() {
		List<CtField<?>> result = new ArrayList<>();
		result.addAll(getEnumValues());
		result.addAll(super.getFields());
		return result;
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
	public CtTypeReference<?> getSuperclass() {
		return getFactory().Type().createReference(Enum.class);
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
			factory.Method().createParameter(valuesMethod, factory.Type().STRING, "name");
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
}
