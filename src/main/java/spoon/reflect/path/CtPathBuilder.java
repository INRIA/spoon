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

	private List<CtPathElement> elements = new LinkedList<CtPathElement>();

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
		return name(CtNamedPathElement.RECURSIVE_WILCARD);
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
	 * @see spoon.reflect.path.CtPathRole
	 */
	public CtPathBuilder role(CtPathRole role, String[]... args) {
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
