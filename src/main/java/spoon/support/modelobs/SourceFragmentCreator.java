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
package spoon.support.modelobs;

import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.support.sniper.internal.ElementSourceFragment;

/**
 * A {@link ChangeCollector}, which builds a tree of {@link ElementSourceFragment}s of {@link CompilationUnit} of the modified element
 * lazily just before the element is changed
 */
public class SourceFragmentCreator extends ChangeCollector {
	@Override
	protected void onChange(CtElement currentElement, CtRole role) {
		if (!currentElement.isParentInitialized()) {
			//parent is not initialized. It is just creation of a temporary element
			//ignore such "change"
			return;
		}
		CompilationUnit cu = currentElement.getPosition().getCompilationUnit();
		if (cu != null) {
			//getOriginalSourceFragment is not only a getter, it actually
			//builds a tree of SourceFragments of compilation unit of the modified element
			cu.getOriginalSourceFragment();
		}
		super.onChange(currentElement, role);
	}
}
