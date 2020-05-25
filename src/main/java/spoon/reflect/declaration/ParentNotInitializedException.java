/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.SpoonException;

/**
 * This exception is thrown when the parent of an element has not been correctly
 * initialized.
 *
 * @author Renaud Pawlak
 * @see CtElement#setParent(CtElement)
 * @see CtElement#getParent()
 * @see CtElement#updateAllParentsBelow()
 */
public class ParentNotInitializedException extends SpoonException {

	private static final long serialVersionUID = 1L;

	public ParentNotInitializedException(String message) {
		super(message);
	}

}
