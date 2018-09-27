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
package spoon.pattern_detector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Delete;
import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.actions.model.Update;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;

import spoon.pattern_detector.internal.UpdateNode;
import spoon.pattern_detector.internal.gumtree.SpoonTree;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtPath;

/**
 * Gum tree information related to a code
 */
class CodeInfo {
	/**
	 * The code of this {@link CodeInfo}
	 */
	final List<? extends CtElement> elements;
	/**
	 * The gum tree of `elements`
	 */
	final ITree codeGumTree;

	CodeInfo(List<? extends CtElement> elements, ITree codeGumTree) {
		super();
		this.elements = elements;
		this.codeGumTree = codeGumTree;
	}

	private CtElement getCommonAncestor() {
		Collection<? extends CtElement> items = elements;
		while (items.size() > 1) {
			Set<CtElement> ancestors = Collections.newSetFromMap(new IdentityHashMap<>());
			for (CtElement item : items) {
				ancestors.add(item.getParent());
			}
			items = ancestors;
		}
		return items.iterator().next();
	}

	@Override
	public String toString() {
		CtElement ancestor = getCommonAncestor();
		CtPath path = ancestor.getPath();
		CtPath relPathOfFirst = elements.get(0).getPath().relativePath(ancestor);
		return path.toString() + "|" + relPathOfFirst.toString();
	}

	/**
	 * Computes differences between this {@link CodeInfo} and `codeInfo`
	 * @param codeInfo to be compared code
	 * @return {@link List} of edit {@link Action}s
	 */
	List<Action> getGumTreeDifferences(CodeInfo codeInfo) {
		ITree t1 = codeGumTree;
		ITree t2 = codeInfo.getGumTree();
		MappingStore mappings = new MappingStore();
		final Matcher matcher = new CompositeMatchers.ClassicGumtree(t1, t2, mappings);
		matcher.match();

		final ActionGenerator actionGenerator = new ActionGenerator(t1, t2, matcher.getMappings());
		List<Action> actions = actionGenerator.generate();
		if (t1.getParent() != null) {
			t1.setParent(null);
		}
		if (t2.getParent() != null) {
			t2.setParent(null);
		}
		actions = removeTransitiveActions(actions);
		//add action mapping to nodes of second tree
		List<Action> mappedActions = new ArrayList<>(actions.size());
		for (Action action : actions) {
			ITree srcNode = action.getNode();
			if (action instanceof Delete) {
			} else if (action instanceof Insert) {
			} else if (action instanceof Update) {
				srcNode = getUsefullParent(srcNode);
				ITree dest = mappings.getDst(srcNode);
				action = new UpdateNode(srcNode, dest);
			} else if (action instanceof Move) {
			}
			mappedActions.add(action);
		}
		return mappedActions;
	}

	private ITree getUsefullParent(ITree node) {
		Object value = ((SpoonTree) node.getParent()).getValue();
		if (value instanceof CtLiteral) {
			//pattern parameter has to be CtLiteral element instead of it's value
			return node.getParent();
		}
		return node;
	}

	ITree getGumTree() {
		return codeGumTree;
	}
	/**
	 * The delete of parent node causes that each child node has delete action too.
	 * This function keeps only action, which deletes parent node. Actions about delete of children are removed
	 * @param actions list of all actions
	 * @return list of root actions only
	 */
	private List<Action> removeTransitiveActions(List<Action> actions) {
		Set<ITree> actionNodes = Collections.newSetFromMap(new IdentityHashMap<>());
		for (Action action : actions) {
			actionNodes.add(action.getNode());
		}
		List<Action> rootActions = new ArrayList<>();
		for (Action action : actions) {
			if (actionNodes.contains(action.getNode().getParent())) {
				//there is another action which manipulates parent node of this action. Ignore this action
				continue;
			}
			rootActions.add(action);
		}
		return rootActions;
	}
}
