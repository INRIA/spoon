/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
