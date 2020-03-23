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

import static spoon.support.sniper.internal.ElementSourceFragment.isSpaceFragment;

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

	@Override
	protected void printOriginSpacesUntilFragmentIndex(int index) {
		super.printOriginSpacesUntilFragmentIndex(getLastWhiteSpaceBefore(index), index);
	}

	/**
	 * @return index of last child fragment which contains space, which is before `index`
	 */
	private int getLastWhiteSpaceBefore(int index) {
		for (int i = index - 1; i >= 0; i--) {
			SourceFragment fragment = childFragments.get(i);
			if (isSpaceFragment(fragment)) {
				continue;
			}
			return i + 1;
		}
		return 0;
	}
}
