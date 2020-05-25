/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.refactoring;

/**
 * Defines basic contract of all refactoring implementations.<br>
 * Contract: to process a required refactoring.<br>
 * Usage:<br>
 * <pre>
 * SomeRefactoring r = new SomeRefactoring();
 * //configure refactoring by calling setters on `r`
 * r.refactor();
 * </pre>
 * See child interfaces, which implements other supported refactoring methods
 */
public interface CtRefactoring {
	/**
	 * Process refactoring operation
	 */
	void refactor();
}
