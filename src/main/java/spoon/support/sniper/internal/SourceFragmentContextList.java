/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

import spoon.reflect.declaration.CtElement;

import java.util.List;

/**
 * Handles printing of changes of the ordered list of elements.
 * E.g. list of type members of type
 * Such lists must be printed in same order like they are in defined in Spoon model.
 */
public class SourceFragmentContextList extends AbstractSourceFragmentContextCollection {
	/**
	 * @param mutableTokenWriter {@link MutableTokenWriter}, which is used for printing
	 * @param element the {@link CtElement} whose list attribute is handled
	 * @param fragments the List of fragments, which represents whole list of elements. E.g. body of method or all type members of type
	 * @param changeResolver {@link ChangeResolver}, which can be used to detect changes of list items
	 */
	public SourceFragmentContextList(MutableTokenWriter mutableTokenWriter, CtElement element, List<SourceFragment> fragments, ChangeResolver changeResolver) {
		super(mutableTokenWriter, fragments, changeResolver);
	}

	@Override
	protected int findIFragmentIndexCorrespondingToEvent(PrinterEvent event) {
		if (event instanceof ElementPrinterEvent) {
			// in case of collection search for exact item of the collection
			ElementPrinterEvent elementEvent = (ElementPrinterEvent) event;
			return findIndexOfNextChildTokenOfElement(elementEvent.getElement());
		}
		return super.findIFragmentIndexCorrespondingToEvent(event);
	}

}
