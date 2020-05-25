/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.path;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.impl.CtNamedPathElement;
import spoon.reflect.path.impl.CtPathElement;
import spoon.reflect.path.impl.CtPathImpl;
import spoon.reflect.path.impl.CtRolePathElement;
import spoon.reflect.path.impl.CtTypedNameElement;

import java.util.LinkedList;
import java.util.List;

/**
 * This builder allow to create some CtPath.
 * <p>
 * Some examples:
 * <p>
 * To create a CtPath that match with any method in fr.spoon.Launcher:
 * <pre>
 * {@code
 * new CtPathBuilder().name("fr").name("spoon").name("Launcher").type("method");
 * }
 * </pre>
 * Created by nicolas on 10/06/2015.
 */
public class CtPathBuilder {

	private List<CtPathElement> elements = new LinkedList<>();

	/**
	 * Add a name matcher to this path.
	 *
	 * @param name
	 * @param args
	 * @return
	 */
	public CtPathBuilder name(String name, String[]... args) {
		CtNamedPathElement e = new CtNamedPathElement(name);
		if (args != null) {
			for (String[] arg : args) {
				e.addArgument(arg[0], arg[1]);
			}
		}
		elements.add(e);
		return this;
	}

	/**
	 * Add a simple wildcard. Match only on elements child of current one.
	 */
	public CtPathBuilder wildcard() {
		return name(CtNamedPathElement.WILDCARD);
	}

	/**
	 * Add a recursive wildcard. It match on any child and sub-childs.
	 */
	public CtPathBuilder recursiveWildcard() {
		return name(CtNamedPathElement.RECURSIVE_WILDCARD);
	}

	/**
	 * Match on element of a given type.
	 */
	public <T extends CtElement> CtPathBuilder type(Class<T> type, String[]... args) {
		CtTypedNameElement e = new CtTypedNameElement(type);
		if (args != null) {
			for (String[] arg : args) {
				e.addArgument(arg[0], arg[1]);
			}
		}
		elements.add(e);
		return this;
	}

	/**
	 * Match on elements by their role.
	 *
	 * @see CtRole
	 */
	public CtPathBuilder role(CtRole role, String[]... args) {
		CtRolePathElement e = new CtRolePathElement(role);
		if (args != null) {
			for (String[] arg : args) {
				e.addArgument(arg[0], arg[1]);
			}
		}
		elements.add(e);
		return this;
	}

	/**
	 * Build the CtPath
	 */
	public CtPath build() {
		CtPathImpl path = new CtPathImpl();
		for (CtPathElement el : elements) {
			path.addLast(el);
		}
		return path;
	}
}
