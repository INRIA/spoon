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

import java.util.Collection;

public abstract class CollectionContext<T extends Collection<?>> extends Context {
	protected final T copyOfTheCollection;

	public CollectionContext(CtElement element, CtRole role, T copyOfTheCollection) {
		super(element, role);
		this.copyOfTheCollection = copyOfTheCollection;
	}
}
