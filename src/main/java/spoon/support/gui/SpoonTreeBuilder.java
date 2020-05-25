/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.gui;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtScanner;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayDeque;
import java.util.Deque;

public class SpoonTreeBuilder extends CtScanner {
	Deque<DefaultMutableTreeNode> nodes;

	DefaultMutableTreeNode root;

	public SpoonTreeBuilder() {
		root = new DefaultMutableTreeNode("Spoon Tree Root");
		nodes = new ArrayDeque<>();
		nodes.push(root);
	}

	private void createNode(Object o, CtRole roleInParent) {
		String prefix = roleInParent == null ? "" : roleInParent.getCamelCaseName() + ": ";
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(o) {
			private static final long serialVersionUID = 1L;

			private String getASTNodeName() {
				// the end user needs to know the interface, not the implementation
				return getUserObject().getClass().getSimpleName().replaceAll("Impl$", "");
			}

			@Override
			public String toString() {
				String nodeName;
				if (getUserObject() instanceof CtNamedElement) {
					nodeName = getASTNodeName()
							+ " - "
							+ ((CtNamedElement) getUserObject())
							.getSimpleName();
				} else {
					String objectRepresentation;
					try {
						objectRepresentation = getUserObject().toString();
					} catch (Exception e) {
						objectRepresentation = "Failed:" + e.getMessage();
					}
					nodeName = getASTNodeName() + " - "	+ objectRepresentation;
				}
				return prefix + nodeName;
			}
		};
		nodes.peek().add(node);
		nodes.push(node);
	}

	private CtRole roleInParent;
	@Override
	public void scan(CtRole role, CtElement element) {
		roleInParent = role;
		super.scan(role, element);
	}

	@Override
	public void enter(CtElement element) {
		createNode(element, roleInParent);
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
