/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.chain;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;

/**
 * Responsible for performing an action when a scanner enters/exits a node while scanning the AST.
 */
public interface CtScannerListener {
	/**
	 * Called before the scanner enters an element
	 *
	 * @param element the element about to be scanned.
	 * @return a {@link ScanningMode} that drives how the scanner processes this element and its children.
	 * For instance, returning {@link ScanningMode#SKIP_ALL} causes that element and all children to be skipped and {@link #exit(CtElement)} are be NOT called for that element.
	 */
	default ScanningMode enter(CtElement element) {
		return ScanningMode.NORMAL;
	}

	/**
	 * Called before the scanner enters an element
	 *
	 * @param role the {@link CtRole}, which `element` has in its parent. It is null for the first scanned element
	 * @param element the element about to be scanned.
	 * @return a {@link ScanningMode} that drives how the scanner processes this element and its children.
	 * For instance, returning {@link ScanningMode#SKIP_ALL} causes that element and all children to be skipped and {@link #exit(CtElement)} are be NOT called for that element.
	 */
	default ScanningMode enter(CtRole role, CtElement element) {
		return enter(element);
	}

	/**
	 * This method is called after the element and all its children have been visited.
	 * This method is NOT called if an exception is thrown in {@link #enter(CtElement)} or during the scanning of the element or any of its children element.
	 * This method is NOT called for an element for which {@link #enter(CtElement)} returned {@link ScanningMode#SKIP_ALL}.
	 *
	 * @param element the element that has just been scanned.
	 */
	default void exit(CtElement element) {
	}
	/**
	 * This method is called after the element and all its children have been visited.
	 * This method is NOT called if an exception is thrown in {@link #enter(CtElement)} or during the scanning of the element or any of its children element.
	 * This method is NOT called for an element for which {@link #enter(CtElement)} returned {@link ScanningMode#SKIP_ALL}.
	 *
	 * @param role the {@link CtRole}, which `element` has in its parent. It is null for the first scanned element
	 * @param element the element that has just been scanned.
	 */
	default void exit(CtRole role, CtElement element) {
		exit(element);
	}
}
