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
package spoon.reflect.meta.impl;

import java.util.ArrayList;
import java.util.List;

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.path.CtRole;

/**
 * Provides a {@link RoleHandler} implementation for the pair of {@link CtElement} implementation and {@link CtRole}
 * The returned {@link RoleHandler} can be then used to manipulate value of attribute represented by {@link CtRole} on the {@link CtElement} instance
 */
public class RoleHandlerHelper {
	private RoleHandlerHelper() {
	}

	@SuppressWarnings("unchecked")
	private static final List<RoleHandler>[] roleHandlers = new List[CtRole.values().length];
	static {
		for (int i = 0; i < roleHandlers.length; i++) {
			roleHandlers[i] = new ArrayList<>();
		}
		for (RoleHandler rh : ModelRoleHandlers.roleHandlers) {
			roleHandlers[rh.getRole().ordinal()].add(0, rh);
		}
	}

	/**
	 * @param targetClass the class of the to be manipulated node
	 * @param role defines the to be manipulated attribute
	 * @return {@link RoleHandler} implementation which knows how to manipulate the attribute of {@link CtRole} on `targetClass`
	 * or throws exception if such role does not exists on the `targetClass`
	 */
	public static RoleHandler getRoleHandler(Class<? extends CtElement> targetClass, CtRole role) {
		RoleHandler rh = getOptionalRoleHandler(targetClass, role);
		if (rh == null) {
			throw new SpoonException("The element of class " + targetClass + " does not have CtRole." + role.name());
		}
		return rh;
	}
	/**
	 * @param targetClass the class of the to be manipulated node
	 * @param role defines the to be manipulated attribute
	 * @return {@link RoleHandler} implementation which knows how to manipulate the attribute of {@link CtRole} on `targetClass`
	 * or returns null if such role does not exists on the `targetClass`
	 */
	public static RoleHandler getOptionalRoleHandler(Class<? extends CtElement> targetClass, CtRole role) {
		List<RoleHandler> handlers = roleHandlers[role.ordinal()];
		for (RoleHandler ctRoleHandler : handlers) {
			if (ctRoleHandler.getTargetType().isAssignableFrom(targetClass)) {
				return ctRoleHandler;
			}
		}
		return null;
	}
}
