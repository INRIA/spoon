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
