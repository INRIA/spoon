/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

import spoon.compiler.Environment;
import spoon.reflect.visitor.PrinterHelper;

/**
 * Extension of {@link PrinterHelper}, which allows direct printing of source fragments
 */
public class DirectPrinterHelper extends PrinterHelper {

	DirectPrinterHelper(Environment env) {
		super(env);
	}

	/**
	 * Prints `text` directly into output buffer ignoring any Environment rules.
	 * @param text to be printed string
	 */
	public void directPrint(String text) {
		autoWriteTabs();
		int len = text.length();
		for (int i = 0; i < len; i++) {
			char c = text.charAt(i);
			//avoid automatic writing of tabs in the middle of text
			shouldWriteTabs = false;
			write(c);
		}
	}

	/**
	 * Allows to set the protected field of {@link PrinterHelper#shouldWriteTabs}.
	 *
	 * @param shouldWriteTabs true if we just printed EndOfLine and we should print tabs if next character is not another EndOfLine
	 */
	void setShouldWriteTabs(boolean shouldWriteTabs) {
		this.shouldWriteTabs = shouldWriteTabs;
	}
}
