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
	protected int findIndexOfNextChildTokenOfEvent(PrinterEvent event) {
		if (event instanceof ElementPrinterEvent) {
			// in case of collection search for exact item of the collection
			ElementPrinterEvent elementEvent = (ElementPrinterEvent) event;
			return findIndexOfNextChildTokenOfElement(elementEvent.getElement());
		}
		return super.findIndexOfNextChildTokenOfEvent(event);
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
