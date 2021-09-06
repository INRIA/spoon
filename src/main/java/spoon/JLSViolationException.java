/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon;
/**
 * This exception is thrown when an operation on a {@link spoon.reflect.declaration.CtElement} transfers it in an invalid state.
 * <p>
 * An invalid state is a state that is not conform to the JLS. For example, a {@link spoon.reflect.declaration.CtMethod} is in an invalid state if it is abstract and has a body.
 */
public class JLSViolationException extends SpoonException {

	/**
	 * Creates a new JLSViolationException with the given message.
	 * @param msg  the reason of the exception.
	 */
	public JLSViolationException(String msg) {
		super(msg);
	}

}
