/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.support.gui;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.visitor.CtScanner;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayDeque;
import java.util.Deque;

public class SpoonTreeBuilder extends CtScanner {
	Deque<DefaultMutableTreeNode> nodes;

	DefaultMutableTreeNode root;

	public SpoonTreeBuilder() {
		super();
		root = new DefaultMutableTreeNode("Spoon Tree Root");
		nodes = new ArrayDeque<>();
		nodes.push(root);
	}

	private void createNode(Object o) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(o) {
			private static final long serialVersionUID = 1L;

			private String getASTNodeName() {
				// the end user needs to know the interface, not the implementation
				return getUserObject().getClass().getSimpleName().replaceAll("Impl$", "");
			}

			@Override
			public String toString() {
				if (getUserObject() instanceof CtNamedElement) {
					return getASTNodeName()
							+ " - "
							+ ((CtNamedElement) getUserObject())
							.getSimpleName();
				}
				return getASTNodeName() + " - "
						+ getUserObject().toString();
			}
		};
		nodes.peek().add(node);
		nodes.push(node);
	}

	@Override
	public void enter(CtElement element) {
		createNode(element);
		super.enter(element);
	}

	@Override
	public void exit(CtElement element) {
		nodes.pop();
		super.exit(element);
	}

	public DefaultMutableTreeNode getRoot() {
		return root;
	}

}
