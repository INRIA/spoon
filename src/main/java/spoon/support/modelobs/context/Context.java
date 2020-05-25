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

/**
 * defines the context of an action
 */
public abstract class Context {
	private CtElement elementWhereChangeHappens;
	private CtRole changedProperty;

	public Context(CtElement element, CtRole changedProperty) {
		this.elementWhereChangeHappens = element;
		this.changedProperty = changedProperty;
	}

	/**
	 * the changed parent
	 * @return the changed parent
	 */
	public CtElement getElementWhereChangeHappens() {
		return elementWhereChangeHappens;
	}

	/**
	 * the role that has been modified
	 * @return the role that has been modified
	 */
	public CtRole getChangedProperty() {
		return changedProperty;
	}
}
