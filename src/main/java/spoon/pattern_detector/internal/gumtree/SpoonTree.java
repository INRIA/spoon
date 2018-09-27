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

import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.Tree;

import spoon.reflect.path.CtRole;

/**
 * Extension of Tree, which maps to node or attribute value of Spoon AST
 */
public class SpoonTree extends Tree {
	private final Object value;

	public SpoonTree(int type, String label, Object value) {
		super(type, label);
		this.value = value;
	}

	protected SpoonTree(SpoonTree other) {
		super(other);
		this.value = other.value;
	}

	public Object getValue() {
		return value;
	}

	public Tree deepCopy() {
		Tree copy = new SpoonTree(this);
		for (ITree child : getChildren()) {
			copy.addChild(child.deepCopy());
		}
		return copy;
	}

	@Override
	public String toShortString() {
		CtRole role = getRole();
		String roleName = role == null ? "root" : role.name();
		String label = getLabel();
		if (label == null) {
			label = "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(roleName).append(":\"").append(label).append("\"");
		if (children.size() > 0) {
			sb.append("[").append(children.size()).append("]");
		}
		if (value != null) {
			String className = value.getClass().getSimpleName();
			sb.append(" ").append(className).append("->").append(value.toString());
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return toShortString();
	}

	public CtRole getRole() {
		int type = getType();
		if (type < 0) {
			return null;
		}
		return CtRole.values()[type];
	}

	@Override
	public Object getMetadata(String key) {
		if (key == SpoonGumTreeBuilder.SPOON_OBJECT) {
			return value;
		}
		return super.getMetadata(key);
	}

	@Override
	public Object setMetadata(String key, Object value) {
		if (key == SpoonGumTreeBuilder.SPOON_OBJECT) {
			throw new UnsupportedOperationException();
		}
		return super.setMetadata(key, value);
	}
}
