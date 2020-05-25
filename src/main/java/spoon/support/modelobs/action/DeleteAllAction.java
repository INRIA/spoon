/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.modelobs.action;

import spoon.support.modelobs.context.Context;

import java.util.Collection;
import java.util.Map;

/**
 * defines the delete all action.
 * @param <T>
 */
public class DeleteAllAction<T> extends DeleteAction<T> {

	public DeleteAllAction(Context context, Collection oldValue) {
		super(context, (T) oldValue);
	}

	public DeleteAllAction(Context context, Map oldValue) {
		super(context, (T) oldValue);
	}
}
