/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.chain;

/**
 * Defines how a {@link CtScannerListener} drives the scanning of {@link spoon.reflect.visitor.EarlyTerminatingScanner}
 */
public enum ScanningMode {
	/**
	 * Continue with scanning in a normal way, the current element and all children are visited.
	 */
	NORMAL(true, true),
	/**
	 * Skip the current element and skip all its children.
	 */
	SKIP_ALL(false, false),
	/**
	 * Visit current element but skips all its children.
	 */
	SKIP_CHILDREN(true, false);

	public final boolean visitElement;
	public final boolean visitChildren;

	ScanningMode(boolean visitElement, boolean visitChildren) {
		this.visitElement = visitElement;
		this.visitChildren = visitChildren;
	}
}
