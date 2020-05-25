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
 * defines an action change on the model
 */
public abstract class Action {
	private final Context context;

	Action(Context context) {
		this.context = context;
	}

	/**
	 * get the changed value of the model
	 * @param <T> the type of the element
	 * @return the changed value
	 */
	public abstract <T> T getChangedValue();

	/**
	 * get the context of the change
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}
}
