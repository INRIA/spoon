/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor.replace;

import spoon.SpoonException;

/**
 * Thrown when replacing of element by another element is not possible
 */
public class InvalidReplaceException extends SpoonException {

	public InvalidReplaceException() {
	}

	public InvalidReplaceException(String msg) {
		super(msg);
	}

	public InvalidReplaceException(Throwable e) {
		super(e);
	}

	public InvalidReplaceException(String msg, Throwable e) {
		super(msg, e);
	}

}
