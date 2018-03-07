/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
import spoon.pattern.node.Node;

/**
 * Defines what happens when before explicitly added {@link Node} has to be replaced by another {@link Node}
 */
public enum ConflictResolutionMode {
	/**
	 * throw {@link SpoonException}
	 */
	FAIL,
	/**
	 * get rid of old {@link Node} and use new {@link Node} instead
	 */
	USE_NEW_NODE,
	/**
	 * keep old {@link Node} and ignore try to add any new {@link Node}
	 */
	KEEP_OLD_NODE
}
