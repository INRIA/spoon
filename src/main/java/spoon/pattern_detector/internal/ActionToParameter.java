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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Update;

import spoon.SpoonException;
import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.pattern_detector.internal.gumtree.SpoonTree;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.support.util.internal.MapUtils;

/**
 * Transforms gumtree action to Pattern parameter
 */
public class ActionToParameter {
	private final PatternBuilder patternBuilder;
	private int nextParamIdx = 0;
	private Map<String, String> nameToParamName = new HashMap<>();
	private Map<String, List<Action>> paramNameToAllActions = new HashMap<>();

	public ActionToParameter(PatternBuilder patternBuilder) {
		super();
		this.patternBuilder = patternBuilder;
	}

	public void applyAction(Action action, List<Action> allActions) {
		if (action instanceof Update) {
			applyUpdate((Update) action, allActions);
			return;
		}
		throw new SpoonException("GumTree action of type " + action.getClass() + " Not supported");
	}

	private void applyUpdate(Update action, List<Action> allActions) {
		SpoonTree node = (SpoonTree) action.getNode();
		SpoonTree parentNode = (SpoonTree) action.getNode().getParent();
		CtElement parentElement = (CtElement) parentNode.getValue();
		CtRole modifiedRole = node.getRole();
		if (node.getValue() instanceof CtElement) {
			CtElement element = (CtElement) node.getValue();
			applyUpdateOfElement(parentElement, modifiedRole, element, allActions);
		} else {
			Object value = node.getValue();
			applyUpdateOfPrimitiveValue(parentElement, modifiedRole, value);
		}
	}

	/**
	 * Make pattern parameter for `parentElement` with role `modifiedRole` and origin value `element`
	 */
	private void applyUpdateOfElement(CtElement parentElement, CtRole modifiedRole, CtElement element, List<Action> allActions) {
		String paramName = detectParamName(element, allActions);
		//detect whether we need a new parameter or we can reuse existing
		patternBuilder.configurePatternParameters(pb -> {
			pb.parameter(paramName).byElement(element);
		});
//		throw new SpoonException("Role " + modifiedRole + " not supported on reference.");
	}

	/**
	 * Make pattern parameter for `parentElement` with role `modifiedRole` and origin primitive `value`
	 */
	private void applyUpdateOfPrimitiveValue(CtElement parentElement, CtRole modifiedRole, Object value) {
		if (modifiedRole == CtRole.NAME) {
			if (value instanceof String) {
				String name = (String) value;
				String paramName = MapUtils.getOrCreate(nameToParamName, name, this::getNextParamName);
				patternBuilder.configurePatternParameters(pb -> {
					pb.parameter(paramName).byRole(CtRole.NAME, parentElement);
				});
				return;
			}
		}
		throw new SpoonException("Primitive CtRole." + modifiedRole + " not supported on " + parentElement.getClass() + " and value: " + String.valueOf(value));
	}

	private String detectParamName(CtElement element, List<Action> allActions) {
		//we can reuse older parameter if it has same parameter value in definition node and this node for primary pattern and each matching code
		for (Map.Entry<String, List<Action>> e : paramNameToAllActions.entrySet()) {
			String paramName = e.getKey();
			List<Action> actionsOfParameter = e.getValue();
			if (actionValuesEquals(actionsOfParameter, allActions, (element1, element2) -> {
				return element1.equals(element2);
			})) {
				//all current parameter values are equal to parameter values of parameter with `paramName`. We can use that existing parameter
				return paramName;
			}
		}
		String paramName = getNextParamName();
		paramNameToAllActions.put(paramName, allActions);
		return paramName;
	}

	private boolean actionValuesEquals(List<Action> actions1, List<Action> actions2, BiPredicate<CtElement, CtElement> consumer) {
		int length = Math.max(actions1.size(), actions2.size());
		if (!consumer.test(getPrimaryElement(actions1), getPrimaryElement(actions2))) {
			return false;
		}
		for (int i = 0; i < length; i++) {
			if (!consumer.test(getSecondaryElement(actions1, i), getSecondaryElement(actions2, i))) {
				return false;
			}
		}
		return true;
	}

	private CtElement getPrimaryElement(List<Action> actions) {
		//take the value from primary code
		Action action = getFirstNotNull(actions);
		return (CtElement) ((SpoonTree) action.getNode()).getValue();
	}

	private CtElement getSecondaryElement(List<Action> actions, int idx) {
		if (idx < actions.size()) {
			//take Action with differences for matched code of index `idx`;
			Action action = actions.get(idx);
			if (action != null) {
				//there is update action for matched code of index `idx`;
				return (CtElement) ((SpoonTree) ((UpdateNode) action).getNewNode()).getValue();
			}
		}
		//there is no action for code of index `idx` because this code is same as primary code at this position
		//take the value from primary code
		return getPrimaryElement(actions);
	}

	private Action getActionByIdx(List<Action> actions, int idx) {
		return null;
	}

	private String getNextParamName() {
		return "param" + String.valueOf(nextParamIdx++);
	}

	public Pattern build() {
		return patternBuilder.build();
	}

	public static <T> T getFirstNotNull(List<T> list) {
		if (list == null) {
			return null;
		}
		for (T t : list) {
			if (t != null) {
				return t;
			}
		}
		throw new SpoonException("The list is empty");
	}
}
