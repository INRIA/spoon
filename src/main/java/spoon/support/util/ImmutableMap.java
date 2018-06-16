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
package spoon.support.util;

import java.util.Map;

/**
 *
 * An immutable map.
 * (eg unmodifiable storage of parameter name-value pairs).
 * The values may be primitive values or List, Set, Map of values.
 * All internal containers are unmodifiable too.
 *
 * Internal class only, not in the public API.
 */
public interface ImmutableMap {

	/**
	 * @param parameterName to be checked parameter name
	 * @return true if there is defined some value for the parameter. null can be a value too
	 */
	boolean hasValue(String parameterName);

	/**
	 * @param parameterName the name of the parameter
	 * @return a value of the parameter under the name `parameterNamer
	 */
	Object getValue(String parameterName);

	/**
	 * @param parameterName to be set parameter name
	 * @param value the new value
	 * @return copies this {@link ImmutableMap}, sets the new value there and returns that copy
	 */
	ImmutableMap putValue(String parameterName, Object value);

	/**
	 * @return underlying unmodifiable Map&lt;String, Object&gt;
	 */
	Map<String, Object> asMap();

	/**
	 * @return a new instance of {@link ImmutableMap}, which inherits all values from this {@link ImmutableMap}
	 * Any call of {@link #putValue(String, Object)} is remembered in local Map of parameters.
	 * At the end of process the {@link #getModifiedValues()} can be used to return all the parameters which were changed
	 * after local {@link ImmutableMap} was created
	 */
	ImmutableMap checkpoint();

	/**
	 * @return the modified parameters since last call to {@link #checkpoint()}
	 */
	Map<String, Object> getModifiedValues();
}
