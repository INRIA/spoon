/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
	void print();

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

}
