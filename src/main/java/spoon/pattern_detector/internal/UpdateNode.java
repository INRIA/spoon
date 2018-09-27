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
package spoon.pattern_detector.internal;

import com.github.gumtreediff.actions.model.Update;
import com.github.gumtreediff.tree.ITree;

/**
 * Extension of gum tree {@link Update} action, which contains node from second
 * tree
 */
public class UpdateNode extends Update {

	private ITree newNode;

	public UpdateNode(ITree node, ITree newNode) {
		super(node, newNode.getLabel());
		this.newNode = newNode;
	}

	/**
	 * @return {@link ITree} from second gum tree, which represents new node -
	 *         after update
	 */
	public ITree getNewNode() {
		return newNode;
	}
}
