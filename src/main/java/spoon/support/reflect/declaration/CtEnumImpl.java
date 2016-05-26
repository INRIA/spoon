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

import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CtEnumImpl<T extends Enum<?>> extends CtClassImpl<T> implements CtEnum<T> {
	private static final long serialVersionUID = 1L;

	private List<CtEnumValue<?>> enumValues = CtElementImpl.<CtEnumValue<?>>emptyList();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtEnum(this);
	}

	@Override
	public Set<CtMethod<?>> getAllMethods() {
		return getMethods();
	}

	@Override
	public boolean isSubtypeOf(CtTypeReference<?> type) {
		for (CtTypeReference<?> ref : getSuperInterfaces()) {
			if (ref.isSubtypeOf(type)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public <C extends CtEnum<T>> C addEnumValue(CtEnumValue<?> enumValue) {
		if (enumValues == CtElementImpl.<CtEnumValue<?>>emptyList()) {
			enumValues = new ArrayList<CtEnumValue<?>>();
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
	public List<CtField<?>> getValues() {
		List<CtField<?>> result = new ArrayList<CtField<?>>();
		for (CtField<?> field : getEnumValues()) {
			result.add(field);
		}
		return Collections.unmodifiableList(result);
	}

	@Override
	public List<CtField<?>> getFields() {
		List<CtField<?>> result = new ArrayList<CtField<?>>();
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
}
