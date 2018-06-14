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
