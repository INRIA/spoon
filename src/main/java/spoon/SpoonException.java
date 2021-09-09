/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon;

import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.FactoryAccessor;

/** is a generic runtime exception for Spoon */
public class SpoonException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	protected static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	public SpoonException() {
	}
	public SpoonException(String msg) {
		super(msg);
	}
	public SpoonException(Throwable e) {
		super(e);
	}
	public SpoonException(String msg, Throwable e) {
		super(msg, e);
	}

	public static void handleJLSViolation(FactoryAccessor holder, String reason) {
			if (holder.getFactory() != null && holder.getFactory().getEnvironment() != null
					&& holder.getFactory().getEnvironment().getIgnoreSyntaxErrors()) {
				throw new JLSViolation(reason);
			} else {
				LOGGER.info("An element is not compliant to the JLS. See: {}", reason);
			}
	}
}
