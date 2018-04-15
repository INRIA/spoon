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
package spoon.pattern.parameter;

import java.util.Map;

/**
 * It is unmodifiable storage of parameter name-value pairs.
 * The values may be primitive values or List,Set,Map of values.
 * All internal containers are unmodifiable too.
 */
public interface ParameterValueProvider {

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
	 * @return copies this {@link ParameterValueProvider}, sets the new value there and returns that copy
	 */
	ParameterValueProvider putValueToCopy(String parameterName, Object value);

	/**
	 * @return underlying unmodifiable Map&lt;String, Object&gt;
	 */
	Map<String, Object> asMap();

	/**
	 * @return a new instance of {@link ParameterValueProvider}, which inherits all values from this {@link ParameterValueProvider}
	 * Any call of {@link #putValueToCopy(String, Object)} is remembered in local Map of parameters.
	 * At the end of process the {@link #asLocalMap()} can be used to return all the parameters which were changed
	 * after local {@link ParameterValueProvider} was created
	 */
	ParameterValueProvider createLocalParameterValueProvider();
	/**
	 * @return {@link Map} with all modified parameters after {@link #createLocalParameterValueProvider()} has been called
	 */
	Map<String, Object> asLocalMap();
}
