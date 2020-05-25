/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.path;

import spoon.reflect.CtModelImpl;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.impl.CtPathElement;
import spoon.reflect.path.impl.CtPathImpl;
import spoon.reflect.path.impl.CtRolePathElement;
import spoon.reflect.reference.CtReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This builder allow to create some CtPath from CtElements
 *
 * Created by nharrand on 21/02/2018.
 */
public class CtElementPathBuilder {
	private boolean useNamesInPath = true;

	/**
	 * Build absolute path to a CtElement el.
	 *
	 * @throws CtPathException is thrown when root is not a parent of el.
	 *
	 * @param el : the element to which the CtPath leads to
	 * @return CtPath from model root package to el
	 */
	public CtPath fromElement(CtElement el) throws CtPathException {
		return fromElement(el, el.getParent(CtModelImpl.CtRootPackage.class));
	}
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

				case LIST: {
					//Element needs to be differentiated from its brothers
					if (useNamesInPath) {
						String[] pair = getArg(cur);
						String attrName = pair[0];
						String name = pair[1];
						if (name != null) {
							//the path with name is more readable, so prefer name instead of index
							if (role.getSubRoles().size() > 0) {
								//there are subroles.
								role = role.getMatchingSubRoleFor(cur);
								pathElement = new CtRolePathElement(role);
							}
							pathElement.addArgument(attrName, name);
							//check list to check if argument is unique
							List<CtElement> list = roleHandler.asList(parent);
							//Assumes that List's order is deterministic.
							List<CtElement> filteredList = new ArrayList<>();
							int index = -1;
							for (CtElement item : list) {
								String[] pair2 = getArg(item);
								String attrName2 = pair2[0];
								String name2 = pair2[1];
								if (Objects.equals(name, name2) && Objects.equals(attrName, attrName2)) {
									//we found an element with same name
									if (item == cur) {
										//we found cur element. Remember it's index
										index = filteredList.size();
									}
									filteredList.add(item);
								}
							}
							if (filteredList.size() > 1 && index >= 0) {
								//there is more then one element with that name. Use index too
								pathElement.addArgument("index", String.valueOf(index));
							}
							break;
						}
					}

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
				}
				case SET: {
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
				}
				case MAP: {
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
			}
			cur = parent;
			path.addFirst(pathElement);
		}
		return path;
	}

	private String[] getArg(CtElement item) {
		String name = null;
		String attrName = "name";
		if (item instanceof CtExecutable) {
			name = getSignature((CtExecutable) item);
			attrName = "signature";
		} else if (item instanceof CtNamedElement) {
			name = ((CtNamedElement) item).getSimpleName();
		} else if (item instanceof CtReference) {
			name = ((CtReference) item).getSimpleName();
		}
		return new String[]{attrName, name};
	}

	private static String getSignature(CtExecutable exec) {
		String sign = exec.getSignature();
		if (exec instanceof CtConstructor) {
			int idx = sign.indexOf('(');
			return sign.substring(idx);
		}
		return sign;
	}

	/**
	 * Configures what kind of path is generated for List based attributes<br>
	 * A) #superRole[index=x] - always use index to identify item of List. For example `#typeMember[index=7]`. Such paths are fast.
	 * B) #subRole[name=x] - use simpleName or signature of List item if possible. Use the most specific role too.
	 * 	For example `#field[name=counter]` or `#method[signature=getCounter()]`. Such paths are more readable but slower.
	 * @param useNamesInPath if true then names are used instead of index
	 * @return this to support fluent API
	 */
	public CtElementPathBuilder setUseNamesInPath(boolean useNamesInPath) {
		this.useNamesInPath = useNamesInPath;
		return this;
	}
}
