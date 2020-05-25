/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon;

import java.util.Locale;

/**
 * Types of output.
 */
public enum OutputType {
	/**
	 * Analysis only, models are not pretty-printed to disk.
	 */
	NO_OUTPUT,

	/**
	 * One file per top-level class.
	 */
	CLASSES,

	/**
	 * Follows the compilation units given by the input.
	 */
	COMPILATION_UNITS;

	@Override
	public String toString() {
		return this.name().toLowerCase(Locale.US).replaceAll("_", "");
	}

	/**
	 * Gets the output type from an option string.
	 *
	 * @param string the string, as given in the launcher's options
	 *
	 * @return the corresponding output type, null if no match is found
	 *
	 * @see Launcher#printUsage()
	 */
	public static OutputType fromString(String string) {
		for (OutputType outputType : OutputType.values()) {
			if (outputType.toString().equals(string)) {
				return outputType;
			}
		}
		return null;
	}
}
