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
 * defines the update action
 * @param <T>
 */
public class UpdateAction<T> extends Action {
	private final T oldValue;
	private final T newValue;

	public UpdateAction(Context context, T newValue, T oldValue) {
		super(context);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@Override
	public T getChangedValue() {
		return getNewValue();
	}

	/**
	 * the new value in the model
	 * @return the new value
	 */
	public T getNewValue() {
		return newValue;
	}

	/**
	 * the old value in the model
	 * @return the old value
	 */
	public T getOldValue() {
		return oldValue;
	}
}
