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

import spoon.reflect.declaration.CtElement;

/**
 * Listens for entering/exiting of scanning of AST
 */
public interface CtScannerListener {
	/**
	 * Called before scanning process enters an element
	 * <br>
	 * See {@link ScanningMode} documentation for possible modes
	 * The returning {@link ScanningMode#SKIP_ALL} causes that element and all children are skipped and {@link #exit(CtElement)} will be NOT called for that element.
	 *
	 * @param element the processed element
	 * @return {@link ScanningMode} to drive how scanner processes this element and it's children,
	 */
	ScanningMode enter(CtElement element);

	/**
	 * This method is called after element and all it's children are processed.
	 * This method is NOT called if the exception was thrown in {@link #enter(CtElement)} or during processing of element or any children children
	 * This method is NOT called for element whose {@link #enter(CtElement)} returned {@link ScanningMode#SKIP_ALL}
	 *
	 * @param element the processed element
	 */
	void exit(CtElement element);
}
