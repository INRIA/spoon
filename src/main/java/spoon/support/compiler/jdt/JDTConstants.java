/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler.jdt;

/**
 * Extracts a subset of constants as defined in {@link org.eclipse.jdt.internal.compiler.lookup.TypeConstants}, since
 * this class is only meant for internal use.
 */
public class JDTConstants {

	private JDTConstants() {

	}

	public static final char[] MODULE_INFO_FILE_NAME = "module-info.java".toCharArray();
	public static final char[] MODULE_INFO_CLASS_NAME = "module-info.class".toCharArray();

}
