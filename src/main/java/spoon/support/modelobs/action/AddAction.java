/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.modelobs.action;

import spoon.support.modelobs.context.Context;

public class AddAction<T>  extends Action {
	private T newValue;

	public AddAction(Context context, T newValue) {
		super(context);
		this.newValue = newValue;
	}

	@Override
	public T getChangedValue() {
		return getNewValue();
	}

	/**
	 * Returns the added element
	 * @return the new element
	 */
	public T getNewValue() {
		return newValue;
	}
}
