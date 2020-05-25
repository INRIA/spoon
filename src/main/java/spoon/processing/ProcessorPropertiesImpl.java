/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.processing;

import org.apache.commons.lang3.ClassUtils;

import java.util.HashMap;
import java.util.Map;

public class ProcessorPropertiesImpl implements ProcessorProperties {
	private final Map<String, Object> _properties = new HashMap<>();

	@Override
	public <T> T get(Class<T> type, String name) {
		if (type.isPrimitive()) {
			type = (Class<T>) ClassUtils.primitiveToWrapper(type);
		}
		T result = (T) _properties.get(name);
		if (result == null) {
			return null;
		} else {
			return (type.isAssignableFrom(result.getClass())) ? result : null;
		}
	}

	@Override
	public void set(String name, Object o) {
		_properties.put(name, o);
	}

	/**
	 * Gets the corresponding processor name.
	 */
	@Override
	public String getProcessorName() {
		return (String) _properties.get("__NAME__");
	}

}
