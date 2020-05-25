/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.meta;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.reflect.path.CtRole;

/**
 * Enables client code to get and set a field based on a role for a CtElement.
 *
 * One obtains instances of {@link RoleHandler} using the methods of {@link spoon.reflect.meta.impl.RoleHandlerHelper}.
 *
 * There is one role handler per role of {@link CtRole}, they are set by {@link spoon.reflect.meta.impl.RoleHandlerHelper}.
 */
public interface RoleHandler {
	// the main methods, responsible to get and set the field corresponding to this role
	/**
	 * @param element a element from which the value will be get for this role
	 * @return a value of the element on the role defined by {@link #getRole()}
	 */
	<T, U> U getValue(T element);
	/**
	 * @param element a element whose value will be set for this role
	 * @param value new value, which will be assigned to the element's attribute defined by role defined by {@link #getRole()}
	 */
	<T, U> void setValue(T element, U value);

	// introspection methods
	/**
	 * @return the role handled by this handler
	 */
	CtRole getRole();

	/**
	 * @return the type of the class, which this handler can be applied to (eg CtMethod)
	 */
	Class<?> getTargetType();

	/**
	 * @return the type of returned value defined by {@link #getRole()}
	 */
	Class<?> getValueClass();

	/**
	 * @return the container kind, to know whether an element, a list, a map, etc is returned.
	 */
	ContainerKind getContainerKind();

	// utility methods
	/**
	 * @return a value for this role adapted as a modifiable Collection
	 */
	<T, U> Collection<U> asCollection(T element);

	/**
	 * @return a value for this role adapted as a modifiable Set
	 */
	<T, U> Set<U> asSet(T element);

	/**
	 * @return a value for this role adapted as a modifiable List
	 */
	<T, U> List<U> asList(T element);

	/**
	 * @return a value for this role adapted as a modifiable Map
	 */
	<T, U> Map<String, U> asMap(T element);
}
