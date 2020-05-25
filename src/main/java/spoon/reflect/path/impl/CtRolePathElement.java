/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.path.impl;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtPathException;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * A CtPathElement that define some roles for matching.
 * <p>
 * Differents roles are define :
 * <ul>
 * <li>statement: match on all statements define in the body of an executable</li>
 * <li>parameter: match on parameter of an executable</li>
 * <li>defaultValue: for value of ctFields</li>
 * </ul>
 */
public class CtRolePathElement extends AbstractPathElement<CtElement, CtElement> {

	public static final String STRING = "#";

	private final CtRole role;

	public CtRolePathElement(CtRole role) {
		this.role = role;
	}

	public CtRole getRole() {
		return role;
	}

	@Override
	public String toString() {
		return STRING + getRole().toString() + getParamString();
	}

	private CtElement getFromSet(Collection<?> set, String name) throws CtPathException {
		for (Object o: set) {
			if (o instanceof CtNamedElement) {
				if (((CtNamedElement) o).getSimpleName().equals(name)) {
					return (CtElement) o;
				}
			} else if (o instanceof CtReference) {
				if (((CtReference) o).getSimpleName().equals(name)) {
					return (CtElement) o;
				}
			} else {
				throw new CtPathException();
			}
		}
		//Element is not found in set.
		return null;
	}

	@Override
	public Collection<CtElement> getElements(Collection<CtElement> roots) {
		Collection<CtElement> matchs = new LinkedList<>();
		for (CtElement root : roots) {
			RoleHandler roleHandler = RoleHandlerHelper.getOptionalRoleHandler(root.getClass(), getRole());
			if (roleHandler != null) {
				switch (roleHandler.getContainerKind()) {
					case SINGLE:
						if (roleHandler.getValue(root) != null) {
							matchs.add(roleHandler.getValue(root));
						}
						break;

					case LIST: {
						Collection<CtElement> subMatches;
						if (getArguments().containsKey("name")) {
							String name = getArguments().get("name");
							subMatches = new CtNamedPathElement(name).scanElements(roleHandler.asList(root));
						} else if (getArguments().containsKey("signature")) {
							String sign = getArguments().get("signature");
							subMatches = new CtNamedPathElement(sign).scanElements(roleHandler.asList(root));
						} else {
							subMatches = roleHandler.asList(root);
						}
						if (getArguments().containsKey("index")) {
							int index = Integer.parseInt(getArguments().get("index"));
							if (index < subMatches.size()) {
								matchs.add(new ArrayList<>(subMatches).get(index));
							}
						} else {
							matchs.addAll(subMatches);
						}
						break;
					}
					case SET:
						if (getArguments().containsKey("signature")) {
							String sign = getArguments().get("signature");
							matchs.addAll(new CtNamedPathElement(sign).scanElements(roleHandler.asSet(root)));
						} else if (getArguments().containsKey("name")) {
							String name = getArguments().get("name");
							try {
								CtElement match = getFromSet(roleHandler.asSet(root), name);
								if (match != null) {
									matchs.add(match);
								}
							} catch (CtPathException e) {
								//No element found for name.
							}
						} else {
							matchs.addAll(roleHandler.asSet(root));
						}
						break;

					case MAP:
						if (getArguments().containsKey("key")) {
							String name = getArguments().get("key");
							if (roleHandler.asMap(root).containsKey(name)) {
								matchs.add((CtElement) roleHandler.asMap(root).get(name));
							}
						} else {
							Map<String, CtElement> map = roleHandler.asMap(root);
							matchs.addAll(map.values());
						}
						break;
				}
			}
		}
		return matchs;
	}
}
