/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.refactoring;

import spoon.SpoonException;

/**
 * Thrown when required refactoring would cause model inconsistency
 */
public class RefactoringException extends SpoonException {
	private static final long serialVersionUID = 1L;

	public RefactoringException() {
	}

	public RefactoringException(String msg) {
		super(msg);
	}

	public RefactoringException(Throwable e) {
		super(e);
	}

	public RefactoringException(String msg, Throwable e) {
		super(msg, e);
	}
}
