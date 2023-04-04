/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon;

import spoon.compiler.Environment;
import spoon.processing.FactoryAccessor;

/**
 * This exception is thrown when an operation on a {@link spoon.reflect.declaration.CtElement} transfers it in an invalid state.
 * <p>
 * An invalid state is a state that is not conform to the JLS. For example, a {@link spoon.reflect.declaration.CtMethod} is in an invalid state if it is abstract and has a body.
 */
public class JLSViolation extends SpoonException {

	/**
	 * Creates a new JLSViolation with the given message.
	 * @param msg  the reason of the exception.
	 */
	private JLSViolation(String msg) {
		super(msg);
	}

	/**
	 * Handles a JLSViolation according to the environment settings. If {@link Environment#getIgnoreSyntaxErrors()} is set to true, the exception is ignored.
	 * Otherwise, the exception is thrown.
	 * @param holder  an object with access to factory and environment.
	 * @param reason  the reason for the exception.
	 */
	public static void throwIfSyntaxErrorsAreNotIgnored(FactoryAccessor holder, String reason) {
		if (holder != null && holder.getFactory() != null
				&& holder.getFactory().getEnvironment() != null
				&& !holder.getFactory().getEnvironment().getIgnoreSyntaxErrors()) {
			throw new JLSViolation(reason);
		} else {
			LOGGER.info("An element is not compliant to the JLS. See: {}", reason);
		}
	}
}
