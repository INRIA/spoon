/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.modelobs.context;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;

import java.util.List;

/**
 * defines a list context
 */
public class ListContext extends CollectionContext<List<?>> {
	private final int position;

	public ListContext(CtElement element, CtRole role, List<?> original) {
		this(element, role, original, -1);
	}

	public ListContext(CtElement element, CtRole role, List<?> original, int position) {
		super(element, role, original);
		this.position = position;
	}

	/**
	 * the position where the change has been made (returns -1 if no position is defined).
	 *
	 * @return the position of the change
	 */
	public int getPosition() {
		return position;
	}
}
