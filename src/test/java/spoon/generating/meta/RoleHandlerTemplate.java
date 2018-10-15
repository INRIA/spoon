/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
