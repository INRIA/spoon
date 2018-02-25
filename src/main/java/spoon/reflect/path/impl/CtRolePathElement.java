/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.reflect.path.impl;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtPathException;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtReference;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

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

	public CtElement getFromSet(Set set, String name) throws CtPathException {
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
		throw new CtPathException();
	}

	@Override
	public Collection<CtElement> getElements(Collection<CtElement> roots) {
		Collection<CtElement> matchs = new LinkedList<>();
		for (CtElement root : roots) {
			RoleHandler roleHandler = RoleHandlerHelper.getOptionalRoleHandler(root.getClass(), getRole());
			if (roleHandler != null) {
				switch (roleHandler.getContainerKind()) {
					case SINGLE:
						matchs.add(roleHandler.getValue(root));
						break;

					case LIST:
						if (getArguments().containsKey("index")) {
							int index = Integer.parseInt(getArguments().get("index"));
							matchs.add((CtElement) roleHandler.asList(root).get(index));
						}
						break;

					case SET:
						if (getArguments().containsKey("name")) {
							String name = getArguments().get("name");
							try {
								matchs.add(getFromSet(roleHandler.asSet(root), name));
							} catch (CtPathException e) {
								//System.err.println("[ERROR] Element not found for name: " + name);
								//No element found for name.
							}
						}
						break;

					case MAP:
						if (getArguments().containsKey("key")) {
							String name = getArguments().get("key");
							matchs.add((CtElement) roleHandler.asMap(root).get(name));
						}
						break;
				}
			}
		}
		return matchs;
	}
}
