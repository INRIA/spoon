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
package spoon.reflect.meta;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.reflect.path.CtRole;

/**
 * Has all knowledge about a role of an target type
 */
public interface RoleHandler {
	/**
	 * @return a role of this handler
	 */
	CtRole getRole();
	/**
	 * @return a type of the class, which this handler can be applied to
	 */
	Class<?> getTargetType();
	/**
	 * @param element a element whose value will be get
	 * @return a value of the element on the role defined by {@link #getRole()}
	 */
	<T, U> U getValue(T element);
	/**
	 * @param element a element whose value will be set
	 * @param value new value, which will be assigned to the element's attribute defined by role defined by {@link #getRole()}
	 */
	<T, U> void setValue(T element, U value);
	/**
	 * @return a Class of value of the attribute of {@link #getTargetType()} defined by {@link #getRole()}
	 */
	Class<?> getValueClass();

	/**
	 * @return true if value can contain only one element. It is not a collection or map
	 */
	ContainerKind getContainerKind();

	<T, U> Collection<U> asCollection(T element);
	<T, U> Set<U> asSet(T element);
	<T, U> List<U> asList(T element);
	<T, U> Map<String, U> asMap(T element);
}
