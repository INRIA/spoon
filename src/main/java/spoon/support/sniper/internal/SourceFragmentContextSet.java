/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

import java.util.List;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;

/**
 * Handles printing of changes of the unordered Set of elements.
 * E.g. set of modifiers and annotations or set of super interfaces
 * Such sets must be printed in same order like they were in origin source code
 *
 * If anything is modified (add/remove/modify element) in such collection,
 * then collection is printed in the order defined by {@link DefaultJavaPrettyPrinter}.
 * The not modified items of collection are printed using origin sources
 */
public class SourceFragmentContextSet extends AbstractSourceFragmentContextCollection {
	/**
	 * @param mutableTokenWriter {@link MutableTokenWriter}, which is used for printing
	 * @param element the {@link CtElement} whose list attribute is handled
	 */
	public SourceFragmentContextSet(MutableTokenWriter mutableTokenWriter, CtElement element, List<SourceFragment> fragments, ChangeResolver changeResolver) {
		super(mutableTokenWriter, fragments, changeResolver);
	}
}
