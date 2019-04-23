/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;

/**
 * Knows how to handle actually printed {@link CtElement} or its part
 */
public interface SourceFragmentContext {
	/**
	 * Called when {@link DefaultJavaPrettyPrinter} starts an operation
	 * @param event the {@link DefaultJavaPrettyPrinter} event
	 */
	void onPrintEvent(PrinterEvent event);

	/**
	 * Called when printing using this context is going to finish
	 */
	void onFinished();

	/**
	 * @return true if this context can handle `role`
	 */
	boolean matchesPrinterEvent(PrinterEvent event);
}
