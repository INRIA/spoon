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
package spoon.reflect.path;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.impl.CtPathElement;
import spoon.reflect.path.impl.CtPathImpl;
import spoon.reflect.path.impl.CtRolePathElement;
import spoon.reflect.reference.CtReference;

import java.util.List;
import java.util.Map;

/**
 * This builder allow to create some CtPath from CtElements
 *
 * Created by nharrand on 21/02/2018.
 */
public class CtElementPathBuilder {
	/**
	 * Build path to a CtElement el, from one of its parent.
	 *
	 * @throws CtPathException is thrown when root is not a parent of el.
	 *
	 * @param el : the element to which the CtPath leads to
	 * @param root : Starting point of the CtPath
	 * @return CtPath from root to el
	 */
	public CtPath fromElement(CtElement el, CtElement root) throws CtPathException {
		CtPathImpl path = new CtPathImpl();
		CtElement cur = el;
		while (cur != root) {
			CtElement parent = cur.getParent();
			CtRole role = cur.getRoleInParent();
			if (role == null) {
				throw new CtPathException();
			}
			RoleHandler roleHandler = RoleHandlerHelper.getOptionalRoleHandler(parent.getClass(), role);
			if (roleHandler == null) {
				throw new CtPathException();
			}
			CtPathElement pathElement = new CtRolePathElement(role);
			switch (roleHandler.getContainerKind()) {
				case SINGLE:
					break;

				case LIST:
					//Element needs to be differentiated from its brothers
					List list = roleHandler.asList(parent);
					//Assumes that List's order is deterministic.
					//Can't be replaced by list.indexOf(cur)
					//Because objects must be the same (and not just equals)
					int index = 0;
					for (Object o : list) {
						if (o == cur) {
							break;
						}
						index++;
					}
					pathElement.addArgument("index", index + "");
					break;

				case SET:
					String name;
					if (cur instanceof CtNamedElement) {
						name = ((CtNamedElement) cur).getSimpleName();
					} else if (cur instanceof CtReference) {
						name = ((CtReference) cur).getSimpleName();
					} else {
						throw new CtPathException();
					}
					pathElement.addArgument("name", name);
					break;

				case MAP:
					Map map = roleHandler.asMap(parent);
					String key = null;
					for (Object o : map.keySet()) {
						if (map.get(o) == cur) {
							key = (String) o;
							break;
						}
					}
					if (key == null) {
						throw new CtPathException();
					} else {
						pathElement.addArgument("key", key);
					}
					break;
			}
			cur = parent;
			path.addFirst(pathElement);
		}
		return path;
	}
}
