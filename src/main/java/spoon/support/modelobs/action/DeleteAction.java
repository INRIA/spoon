/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.modelobs.action;

import spoon.support.modelobs.context.Context;

/**
 * defines the delete action
 * @param <T>
 */
public class DeleteAction<T> extends Action {
	private T oldValue;

	public DeleteAction(Context context, T oldValue) {
		super(context);
		this.oldValue = oldValue;
	}

	@Override
	public T getChangedValue() {
		return getRemovedValue();
	}

	/**
	 * Returns the removed element
	 * @return the removed element
	 */
	public T getRemovedValue() {
		return oldValue;
	}
}
