/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.path.impl;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.path.CtPath;
import spoon.reflect.path.CtRole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Default implementation for a CtPath
 */
public class CtPathImpl implements CtPath {

	private LinkedList<CtPathElement> elements = new LinkedList<>();

	public List<CtPathElement> getElements() {
		return elements;
	}

	@Override
	public <T extends CtElement> List<T> evaluateOn(CtElement... startNode) {
		Collection<CtElement> filtered = Arrays.asList(startNode);
		for (CtPathElement element : elements) {
			filtered = element.getElements(filtered);
		}
		return (List<T>) filtered;
	}

	@Override
	public CtElement evaluateOnShadowModel() {
		List<String> classRoleNameList = new LinkedList<>();
		CtType<?> ctType = null;
		for (CtPathElement element : elements) {
			if (element instanceof CtRolePathElement) {    // search by CtRolePathElement
				Collection<String> values = ((CtRolePathElement) element).getArguments().values();
				String val = null;
				if (values.iterator().hasNext()) {
					val = values.iterator().next();
				}
				if (val != null) {
					if (CtRole.SUB_PACKAGE.equals(((CtRolePathElement) element).getRole())
							|| CtRole.CONTAINED_TYPE.equals(((CtRolePathElement) element).getRole())) {
						classRoleNameList.add(val);
					}
					Class<?> cls = getJdkClass(String.join(".", classRoleNameList));
					if (cls != null) {
						if (ctType == null) {
							ctType = new TypeFactory().get(cls);
						} else {
							if (CtRole.METHOD.equals(((CtRolePathElement) element).getRole())) {
								return ctType.getMethodBySignature(val);
							}
							if (CtRole.CONSTRUCTOR.equals(((CtRolePathElement) element).getRole())) {
								return ((CtClass) ctType).getConstructorBySignature(val);
							}
							if (CtRole.FIELD.equals(((CtRolePathElement) element).getRole())) {
								return ctType.getField(val);
							}
						}
					}
				}
			}
		}
		return ctType;
	}

	private Class<?> getJdkClass(String name) {
		name = name.replaceAll("[\\[\\]]", "");
		switch (name) {
			case "byte":
				return byte.class;
			case "int":
				return int.class;
			case "long":
				return long.class;
			case "float":
				return float.class;
			case "double":
				return double.class;
			case "char":
				return char.class;
			case "boolean":
				return boolean.class;
			default:
				try {
					return Class.forName(name);
				} catch (ClassNotFoundException e) {
				}
		}
		return null;
	}

	@Override
	public CtPath relativePath(CtElement parent) {
		List<CtElement> roots = new ArrayList<>();
		roots.add(parent);

		int index = 0;
		for (CtPathElement pathEl : getElements()) {
			if (pathEl.getElements(roots).size() > 0) {
				break;
			}
			index++;
		}
		CtPathImpl result = new CtPathImpl();
		result.elements = new LinkedList<>(elements.subList(index, elements.size()));
		return result;
	}

	public CtPathImpl addFirst(CtPathElement element) {
		elements.addFirst(element);
		return this;
	}

	public CtPathImpl addLast(CtPathElement element) {
		elements.addLast(element);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (CtPathElement element : elements) {
			str.append(element.toString());
		}
		return str.toString();
	}
}
