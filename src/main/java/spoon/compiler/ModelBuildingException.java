/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.compiler;

import spoon.SpoonException;

/**
 * thrown when the Spoon model of a program cannot be built
 */
public class ModelBuildingException extends SpoonException {
	private static final long serialVersionUID = 5029153216403064030L;

	public ModelBuildingException(String msg) {
		super(msg);
	}

	public ModelBuildingException(String msg, Exception e) {
		super(msg, e);
	}
}
