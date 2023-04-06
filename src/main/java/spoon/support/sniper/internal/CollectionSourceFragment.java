/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

import spoon.support.Experimental;

import java.util.List;

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

}
