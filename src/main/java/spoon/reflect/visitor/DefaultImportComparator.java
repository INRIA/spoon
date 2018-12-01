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
