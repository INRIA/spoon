/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;

/**
 * An object that knows how to print {@link PrinterEvent}
 */
public interface SourceFragmentPrinter {
	/**
	 * called when pushed on the stack
	 */
	void onPush();

	/**
	 * Called when {@link DefaultJavaPrettyPrinter} starts an operation
	 * @param event the {@link DefaultJavaPrettyPrinter} event
	 */
	void print(PrinterEvent event);

	/**
	 * Called when printing using this context is going to finish
	 */
	void onFinished();

	/**
	 * @return true if this printer is able to handle this event
	 * That is that we can safely call {@link #print(PrinterEvent)} after
	 * having called this one.
	 */
	boolean knowsHowToPrint(PrinterEvent event);

}
