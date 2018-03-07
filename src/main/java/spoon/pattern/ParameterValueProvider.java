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
package spoon.pattern;

import java.util.Map;

/**
 * During substitution process it provides values of parameters from underlying storage (e.g. from Map or an instance of an class)
 * During matching process it sets values of matched parameters into underlying storage
 * TODO: create ParameterValueProviderFactory which creates appropriate instances of ParameterValueProviders during matching process
 */
public interface ParameterValueProvider {

	/**
	 * @param parameterName to be checked parameter name
	 * @return true if there is defined some value for the parameter. null can be a value too
	 */
	boolean hasValue(String parameterName);
	Object get(String parameterName);
	ParameterValueProvider putIntoCopy(String parameterName, Object value);

	Map<String, Object> asMap();

	ParameterValueProvider createLocalParameterValueProvider();
	Map<String, Object> asLocalMap();
}
