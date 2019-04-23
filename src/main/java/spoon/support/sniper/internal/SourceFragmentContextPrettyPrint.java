/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

/**
 * A {@link SourceFragmentContext}, which prints the element using standard pretty printing
 */
public class SourceFragmentContextPrettyPrint implements SourceFragmentContext {
	/**
	 * This context is used to force normal pretty printing of element
	 */
	public static final SourceFragmentContextPrettyPrint INSTANCE = new SourceFragmentContextPrettyPrint();

	private SourceFragmentContextPrettyPrint() {
	}

	@Override
	public void onPrintEvent(PrinterEvent event) {
		event.print(null);
	}

	@Override
	public void onFinished() {
	}

	@Override
	public boolean matchesPrinterEvent(PrinterEvent event) {
		return true;
	}
}
