/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
