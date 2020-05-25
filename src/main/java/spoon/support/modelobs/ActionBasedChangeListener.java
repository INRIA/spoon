/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.modelobs;

import spoon.support.modelobs.action.Action;
import spoon.support.modelobs.action.AddAction;
import spoon.support.modelobs.action.DeleteAction;
import spoon.support.modelobs.action.DeleteAllAction;
import spoon.support.modelobs.action.UpdateAction;

/**
 * notifies all change on the AST
 */
public interface ActionBasedChangeListener {
	/**
	 * when an element is removed
	 * @param action contains information of the change
	 */
	void onDelete(DeleteAction action);

	/**
	 * when all element are removed
	 * @param action contains information of the change
	 */
	void onDeleteAll(DeleteAllAction action);

	/**
	 * when an element is added
	 * @param action contains information of the change
	 */
	void onAdd(AddAction action);

	/**
	 * when an element is modified
	 * @param action contains information of the change
	 */
	void onUpdate(UpdateAction action);

	/**
	 * when an element is changed
	 * @param action contains information of the change
	 */
	void onAction(Action action);
}
