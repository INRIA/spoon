/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.processing;

/**
 * This exception is used to interrupt a processor during its processing.
 */
public class ProcessInterruption extends RuntimeException {
	public ProcessInterruption() {
	}

	public ProcessInterruption(String message) {
		super(message);
	}

	public ProcessInterruption(String message, Throwable cause) {
		super(message, cause);
	}

	public ProcessInterruption(Throwable cause) {
		super(cause);
	}
}
