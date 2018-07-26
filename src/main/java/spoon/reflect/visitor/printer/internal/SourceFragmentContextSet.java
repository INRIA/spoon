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

import java.util.List;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.printer.internal.SourceFragment;

/**
 * Handles printing of changes of the unordered Set of elements.
 * E.g. set of modifiers and annotations or set of super interfaces
 * Such sets must be printed in same order like they were in origin source code
 *
 * If anything is modified (add/remove/modify element) in such collection,
 * then collection is printed in the order defined by {@link DefaultJavaPrettyPrinter}.
 * The not modified items of collection are printed using origin sources
 */
class SourceFragmentContextSet extends AbstractSourceFragmentContextCollection {
	/**
	 * @param mutableTokenWriter {@link MutableTokenWriter}, which is used for printing
	 * @param element the {@link CtElement} whose list attribute is handled
	 * @param collectionFragment the {@link CollectionSourceFragment}, which represents whole list of elements. E.g. body of method or all type members of type
	 */
	SourceFragmentContextSet(MutableTokenWriter mutableTokenWriter, CtElement element, List<SourceFragment> fragments, ChangeResolver changeResolver) {
		super(mutableTokenWriter, fragments, changeResolver);
	}

	@Override
	public void onPrintEvent(PrinterEvent event) {
		// TODO Auto-generated method stub
		super.onPrintEvent(event);
	}
}
