/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.processing;

/**
 * An interface to retrieve processor properties.
 */
public interface ProcessorProperties {

	/**
	 * Gets the property converted in given type or null (can be an array).
	 */
	<T> T get(Class<T> type, String name);

	/**
	 * Sets the given property.
	 */
	void set(String name, Object o);


	/**
	 * Gets the corresponding processor name.
	 */
	String getProcessorName();

}
