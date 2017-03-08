/**
 * Copyright (C) 2006-2017 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.reflect.visitor.chain;

/**
 * Use in {@link CtScannerListener#enter(spoon.reflect.declaration.CtElement)} to define how to continue with scanning
 */
public enum ScanningMode {
	/**
	 * Continue with scanning in a normal way. Means current element and all children are visited.
	 */
	NORMAL(true, true),
	/**
	 * Skip current element and all it's children.
	 */
	SKIP_ALL(false, false),
	/**
	 * Visit current element but skip all it's children.
	 */
	SKIP_CHILDREN(true, false);

	public final boolean visitElement;
	public final boolean visitChildren;

	ScanningMode(boolean visitElement, boolean visitChildren) {
		this.visitElement = visitElement;
		this.visitChildren = visitChildren;
	}
}
