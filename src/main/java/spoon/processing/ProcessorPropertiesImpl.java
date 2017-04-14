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
package spoon.processing;

import java.util.HashMap;
import java.util.Map;

public class ProcessorPropertiesImpl implements ProcessorProperties {

	private final Map<String, Object> _properties = new HashMap<>();

	public <T> T get(Class<T> type, String name) {
		return (T) _properties.get(name);
	}

	public void set(String name, Object o) {
		_properties.put(name, o);
	}

	/**
	 * Gets the corresponding processor name.
	 */
	public String getProcessorName() {
		return (String) _properties.get("__NAME__");
	}

}
