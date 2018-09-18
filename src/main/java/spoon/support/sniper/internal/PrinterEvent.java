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

import spoon.reflect.cu.SourcePositionHolder;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;

/**
 * Represents an action of PrettyPrinter
 */
public interface PrinterEvent  {

	/**
	 * @return role of printed source code of element in scope of its parent
	 */
	CtRole getRole();

	/**
	 * Prints source code by {@link DefaultJavaPrettyPrinter} ignoring origin {@link SourceFragment}s
	 * @param muted
	 * 		true if origin sources are already printed and we are just calling {@link DefaultJavaPrettyPrinter}
	 * 			to keep it's state consistent.
	 *  	false if {@link DefaultJavaPrettyPrinter} will really print into output.
	 *  	null if `muted` status should be kept as it is
	 */
	void print(Boolean muted);

	/**
	 * We have a source fragment of to be printed element.
	 * Print unmodified parts of this source `fragment`
	 * @param fragment
	 * @param isModified true if at least some part of `SourceFragment` is modified.
	 * 	false if whole `SourceFragment` including all children is not modified.
	 */
	void printSourceFragment(SourceFragment fragment, Boolean isModified);

	/**
	 * @return printed element or null if printing a primitive token
	 */
	SourcePositionHolder getElement();

	/**
	 * @return printed token or null if printing complex element or comment
	 */
	String getToken();

	/**
	 * @return true if printing white space token. It means New line, space or TAB.
	 */
	boolean isWhitespace();
}
