/*
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

package spoon.pattern_detector.internal.gumtree;

import java.util.List;

import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeContext;
import com.github.gumtreediff.tree.TreeUtils;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.Filter;

/**
 * Scanner to create a GumTree's Tree representation for a Spoon AST.
 */
public class SpoonGumTreeBuilder {
	public static final String SPOON_OBJECT = "spoon_object";
	public static final String DESTINATION_NODE = "dest_node";

	private final SpoonTreeContext treeContext = new SpoonTreeContext();
	private Filter<? super CtElement> filter;

	/**
	 * @param roleInParent the role all elements of code in their parent
	 * @param code the list of spoon elements
	 * @return Gum tree of all elements in `code`
	 */
	public ITree getTree(CtRole roleInParent, List<? extends CtElement> code) {
		final ITree root = treeContext.createTree(-1, "", "root", null);
		TreeScanner scanner = new TreeScanner(treeContext, root);
		if (filter != null) {
			scanner.setFilter(filter);
		}
		for (CtElement element : code) {
			scanner.scan(roleInParent, element);
		}
		root.refresh();
		TreeUtils.postOrderNumbering(root);
		TreeUtils.computeHeight(root);
		return root;
	}

	public TreeContext getTreeContext() {
		return treeContext;
	}

	/**
	 * @param filter only elements which matches filter will be transformed to gumtree.
	 * If element doesn't match it is ignored including it's children
	 */
	public SpoonGumTreeBuilder setFilter(Filter<? super CtElement> filter) {
		this.filter = filter;
		return this;
	}
}
