/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import java.util.Comparator;

import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtImportKind;


/**
 * Defines order of imports:
 * 1) imports are ordered alphabetically
 * 2) static imports are last
 */
public class DefaultImportComparator implements Comparator<CtImport> {

	@Override
	public int compare(CtImport imp1, CtImport imp2) {
		int dif = getImportKindOrder(imp1.getImportKind()) - getImportKindOrder(imp2.getImportKind());
		if (dif != 0) {
			return dif;
		}
		String str1 = removeSuffixSemicolon(imp1.toString());
		String str2 = removeSuffixSemicolon(imp2.toString());
		return str1.compareTo(str2);
	}

	private static String removeSuffixSemicolon(String str) {
		if (str.endsWith(";")) {
			return str.substring(0, str.length() - 1);
		}
		return str;
	}

	private int getImportKindOrder(CtImportKind importKind) {
		switch (importKind) {
		case TYPE:
		case ALL_TYPES:
			return 0;
		default:
			return 1;
		}
	}

}
