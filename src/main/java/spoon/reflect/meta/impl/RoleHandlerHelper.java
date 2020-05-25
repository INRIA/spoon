/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.meta.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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

	private static Map<Class<?>, List<RoleHandler>> roleHandlersByClass = new HashMap<>();

	@SuppressWarnings("unchecked")
	private static final List<RoleHandler>[] roleHandlers = new List[CtRole.values().length];
	static {
		for (int i = 0; i < roleHandlers.length; i++) {
			roleHandlers[i] = new ArrayList<>();
		}
		for (RoleHandler rh : ModelRoleHandlers.roleHandlers) {
			roleHandlers[rh.getRole().ordinal()].add(rh);
		}
		Comparator<RoleHandler> cmp = (a, b) -> a.getTargetType().isAssignableFrom(b.getTargetType()) ? 1 : -1;
		for (RoleHandler rh : ModelRoleHandlers.roleHandlers) {
			roleHandlers[rh.getRole().ordinal()].sort(cmp);
		}
	}

	/**
	 * @param targetClass the class of the to be manipulated node
	 * @param role defines the to be manipulated attribute
	 * @return {@link RoleHandler} implementation which knows how to manipulate the attribute of {@link CtRole} on `targetClass`
	 * or throws exception if such role doesn't exist on the `targetClass`
	 */
	public static RoleHandler getRoleHandler(Class<? extends CtElement> targetClass, CtRole role) {
		RoleHandler rh = getOptionalRoleHandler(targetClass, role);
		if (rh == null) {
			throw new SpoonException("The element of class " + targetClass + " does not have CtRole." + role.name());
		}
		return rh;
	}

	/**
	 * @param targetClass the Class of the to be manipulated node
	 * @param role defines the to be manipulated attribute
	 * @return {@link RoleHandler} implementation, which knows how to manipulate the attribute of {@link CtRole} on `targetClass`
	 * or returns null if such role doesn't exist on the `targetClass`
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

	/**
	 * @param targetClass a Class whose handlers we are looking for
	 * @return all {@link RoleHandler}s available for the `targetClass`
	 */
	public static List<RoleHandler> getRoleHandlers(Class<? extends CtElement> targetClass) {
		List<RoleHandler> handlers = roleHandlersByClass.get(targetClass);
		if (handlers == null) {
			List<RoleHandler> modifiableHandlers = new ArrayList<>();
			for (CtRole role : CtRole.values()) {
				RoleHandler roleHandler = getOptionalRoleHandler(targetClass, role);
				if (roleHandler != null) {
					modifiableHandlers.add(roleHandler);
				}
			}
			handlers = Collections.unmodifiableList(modifiableHandlers);
			roleHandlersByClass.put(targetClass, handlers);
		}
		return handlers;
	}

	/**
	 * @param consumer is called for each {@link RoleHandler} of SpoonModel
	 */
	public static void forEachRoleHandler(Consumer<RoleHandler> consumer) {
		for (List<RoleHandler> list : roleHandlers) {
			for (RoleHandler roleHandler : list) {
				consumer.accept(roleHandler);
			}
		}
	}

	/**
	 * @param element the {@link CtElement} whose relation from `element.getParent()` to `element` is needed.
	 * @return {@link RoleHandler} handling relation from `element.getParent()` to `element`
	 */
	public static RoleHandler getRoleHandlerWrtParent(CtElement element) {
		if (element.isParentInitialized() == false) {
			return null;
		}
		CtElement parent = element.getParent();
		CtRole roleInParent = element.getRoleInParent();
		if (roleInParent == null) {
			return null;
		}
		return getRoleHandler(parent.getClass(), roleInParent);
	}
}
