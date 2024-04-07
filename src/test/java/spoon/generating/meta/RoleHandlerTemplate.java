/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.generating.meta;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.path.CtRole;

class RoleHandlerTemplate extends AbstractHandler<Node, ValueType> {

	private RoleHandlerTemplate() {
		super($Role$.ROLE, $TargetType$.class, ValueType.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, U> U getValue(T element) {
		return (U) (Object) castTarget(element).$getterName$();
	}

	@Override
	public <T, U> void setValue(T element, U value) {
		castTarget(element).$setterName$(castValue(value));
	}
}

enum $Role$ {
	ROLE
}

class $TargetType$ {
}

class Node {
	ValueType $getterName$() {
		return null;
	}
	void $setterName$(ValueType value) {
	}
}

class ValueType {
}

class AbstractHandler<T, U> implements RoleHandler {

	AbstractHandler($Role$ role, Class<?> targetClass, Class<?> valueClass) {
	}
	T castTarget(Object e) {
		return null;
	}
	U castValue(Object value) {
		return null;
	}
	@Override
	public CtRole getRole() {
		return null;
	}
	@Override
	public Class<?> getTargetType() {
		return null;
	}
	@Override
	public <T, U> U getValue(T element) {
		return null;
	}
	@Override
	public <T, U> void setValue(T element, U value) {
	}
	@Override
	public Class<?> getValueClass() {
		return null;
	}
	@Override
	public ContainerKind getContainerKind() {
		return null;
	}
	@Override
	public <T, U> Collection<U> asCollection(T element) {
		return null;
	}
	@Override
	public <T, U> Set<U> asSet(T element) {
		return null;
	}
	@Override
	public <T, U> List<U> asList(T element) {
		return null;
	}
	@Override
	public <T, U> Map<String, U> asMap(T element) {
		return null;
	}
}
