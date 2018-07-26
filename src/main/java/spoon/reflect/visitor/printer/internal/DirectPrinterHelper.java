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

import spoon.compiler.Environment;
import spoon.reflect.visitor.PrinterHelper;

/**
 * Extension of {@link PrinterHelper}, which allows direct printing of source fragments
 */
class DirectPrinterHelper extends PrinterHelper {

	/**
	 * the String of spaces and tabs of indentation after EOL written by last directPrint
	 */
//	private String lastDirectIndentation;

	DirectPrinterHelper(Environment env) {
		super(env);
	}

	/**
	 * Prints `text` directly into output buffer ignoring any Environment rules.
	 * @param text to be printed string
	 */
	void directPrint(String text) {
		autoWriteTabs();
//		lastDirectIndentation = getTextIndentation(text);
//		if (lastDirectIndentation != null && lastDirectIndentation.length() > 0) {
//			text.substring(text.length() - lastDirectIndentation.length());
//		}
		int len = text.length();
		for (int i = 0; i < len; i++) {
			char c = text.charAt(i);
			//avoid automatic writing of tabs in the middle of text
			shouldWriteTabs = false;
			write(c);
		}
	}

	/**
	 * @return String of tabs and spaces which ends the text after the EOL
	 */
//	private String getTextIndentation(String text) {
//		int o = text.length();
//		while (o > 0) {
//			o--;
//			char c = text.charAt(o);
//			if (c == ' ' || c == '\t') {
//				continue;
//			} else if (c == '\n' || c == '\r') {
//				return text.substring(o + 1);
//			}
//			return null;
//		}
//		return text;
//	}

	/**
	 * Allows to set the protected field of {@link PrinterHelper}.
	 *
	 * @param shouldWriteTabs true if we just printed EndOfLine and we should print tabs if next character is not another EndOfLine
	 */
	void setShouldWriteTabs(boolean shouldWriteTabs) {
		this.shouldWriteTabs = shouldWriteTabs;
	}
}
