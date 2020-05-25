/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.path;

import spoon.SpoonException;

/**
 *  This exception is throw when there are errors during a CtPath building or evaluation.
 */
public class CtPathException extends SpoonException {
	public CtPathException() {
	}

	public CtPathException(Throwable cause) {
		super(cause);
	}

	public CtPathException(String message) {
		super(message);
	}
}
