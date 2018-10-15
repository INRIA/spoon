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

import spoon.reflect.meta.ContainerKind;
import spoon.reflect.path.CtRole;
import spoon.support.Experimental;

/**
 * {@link SourceFragment} of List or Set of {@link ElementSourceFragment}s which belong to collection role.
 * For example list of Type members or list of parameters, etc.
 * Or set of modifiers and annotations
 */
@Experimental
public class CollectionSourceFragment implements SourceFragment {

	private final List<SourceFragment> items;

	public CollectionSourceFragment(List<SourceFragment> items) {
		this.items = items;
	}

	@Override
	public String getSourceCode() {
		StringBuilder sb = new StringBuilder();
		for (SourceFragment childSourceFragment : items) {
			sb.append(childSourceFragment.getSourceCode());
		}
		return sb.toString();
	}

	/**
	 * @return child source fragments of this collection
	 */
	public List<SourceFragment> getItems() {
		return items;
	}

	@Override
	public String toString() {
		return items.toString();
	}

	/**
	 * @return true if collection contains only children of one role handler with container kind LIST
	 */
	public boolean isOrdered() {
		CtRole role = null;
		for (SourceFragment childSourceFragment : items) {
			if (childSourceFragment instanceof ElementSourceFragment) {
				ElementSourceFragment esf = (ElementSourceFragment) childSourceFragment;
				if (role == null) {
					role = esf.getRoleInParent();
					ContainerKind kind = esf.getContainerKindInParent();
					if (kind != ContainerKind.LIST) {
						return false;
					}
				} else {
					if (role != esf.getRoleInParent()) {
						//the collection contains elements of different roles. It cannot be ordered
						return false;
					}
					//else there is another element of the same role - ok
				}
			}
		}
		//there are only elements of one role of container kind LIST
		return true;
	}
}
