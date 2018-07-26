/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
package spoon.reflect.visitor.printer.internal;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;

/**
 * Knows how to handle actually printed {@link CtElement} or it's part
 */
interface SourceFragmentContext {
	/**
	 * Called when {@link DefaultJavaPrettyPrinter} starts an operation
	 * @param event the {@link DefaultJavaPrettyPrinter} event
	 */
	void onPrintEvent(PrinterEvent event);

	/**
	 * Called when this is child context and parent context is just going to finish it's printing
	 */
	void onFinished();

	/**
	 * @return true if this context can handle `role`
	 */
	boolean matchesPrinterEvent(PrinterEvent event);
}
