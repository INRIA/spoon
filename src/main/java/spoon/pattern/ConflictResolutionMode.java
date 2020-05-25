/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern;

import spoon.SpoonException;
import spoon.pattern.internal.node.RootNode;

/**
 * Defines what happens when a {@link RootNode} has to be replaced by another {@link RootNode}, default in {@link #FAIL}.
 */
public enum ConflictResolutionMode {
	/**
	 * Throw {@link SpoonException} if a conflict happens, it is the default in most cases. But there are some standard Pattern builder algorithms (mainly these which deals with legacy Templates), which are using the other modes.
	 */
	FAIL,
	/**
	 * Get rid of old {@link RootNode} and use new {@link RootNode} instead
	 */
	USE_NEW_NODE,
	/**
	 * Keep old {@link RootNode} and ignore requests to add new {@link RootNode}
	 */
	KEEP_OLD_NODE,
	/**
	 * Add new {@link RootNode} after existing nodes
	 */
	APPEND
}
