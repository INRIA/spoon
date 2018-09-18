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
