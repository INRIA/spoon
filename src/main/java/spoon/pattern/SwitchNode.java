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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import spoon.pattern.matcher.Matchers;
import spoon.pattern.matcher.TobeMatched;
import spoon.reflect.factory.Factory;

/**
 * List of conditional cases
 * {code}
 * if (a) {
 *  ... someStatements if a == true..
 * } else if (b) {
 *  ... someStatements if b == true..
 * } else {
 *  ... someStatements in other cases ...
 * }
 */
public class SwitchNode implements Node {

	private List<CaseNode> cases = new ArrayList<>();

	public SwitchNode() {
		super();
	}

	@Override
	public boolean replaceNode(Node oldNode, Node newNode) {
		for (CaseNode caseNode : cases) {
			if (caseNode.replaceNode(oldNode, newNode)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds another case into this switch statement
	 * @param vrOfExpression if value of this parameter is true then statement has to be used. If vrOfExpression is null, then statement is always used
	 * @param statement optional statement
	 */
	public void addCase(PrimitiveMatcher vrOfExpression, Node statement) {
		cases.add(new CaseNode(vrOfExpression, statement));
	}

	@Override
	public <T> void generateTargets(Factory factory, ResultHolder<T> result, ParameterValueProvider parameters) {
		for (CaseNode case1 : cases) {
			case1.generateTargets(factory, result, parameters);
		}
	}

	@Override
	public void forEachParameterInfo(BiConsumer<ParameterInfo, Node> consumer) {
		for (CaseNode case1 : cases) {
			if (case1.vrOfExpression != null) {
				case1.vrOfExpression.forEachParameterInfo(consumer);
			}
			if (case1.statement != null) {
				case1.statement.forEachParameterInfo(consumer);
			}
		}
	}

	@Override
	public TobeMatched matchTargets(TobeMatched targets, Matchers nextMatchers) {
		boolean hasDefaultCase = false;
		//detect which case is matching - if any
		for (CaseNode case1 : cases) {
			TobeMatched match = case1.matchTargets(targets, nextMatchers);
			if (match != null) {
				return match;
			}
			if (case1.vrOfExpression == null) {
				hasDefaultCase = true;
			}
		}
		//no case matched
		if (hasDefaultCase) {
			//nothing matched and even the default case didn't matched, so whole switch cannot match
			return null;
		}
		/*
		 * else this switch is optional and matches 0 targets - OK, it is match too.
		 * 1) set all expressions to false
		 * 2) match nextMatchers
		 */
		return new CaseNode(null, null).matchTargets(targets, nextMatchers);
	}

	private class CaseNode implements Node {
		/*
		 * is null for the default case
		 */
		private PrimitiveMatcher vrOfExpression;
		private Node statement;
		private CaseNode(PrimitiveMatcher vrOfExpression, Node statement) {
			super();
			this.vrOfExpression = vrOfExpression;
			this.statement = statement;
		}

		@Override
		public boolean replaceNode(Node oldNode, Node newNode) {
			if (vrOfExpression != null) {
				if (vrOfExpression == oldNode) {
					vrOfExpression = (PrimitiveMatcher) newNode;
					return true;
				}
				if (vrOfExpression.replaceNode(oldNode, newNode)) {
					return true;
				}
			}
			if (statement != null) {
				if (statement == oldNode) {
					statement = newNode;
					return true;
				}
				if (statement.replaceNode(oldNode, newNode)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public TobeMatched matchTargets(TobeMatched targets, Matchers nextMatchers) {
			ParameterValueProvider parameters = targets.getParameters();
			//set all switch parameter values following match case. Even no matching case is OK - everything is false then
			for (CaseNode case1 : cases) {
				if (case1.vrOfExpression != null) {
					//set expression of this `if` depending on if this case matched or not
					parameters = case1.vrOfExpression.matchTarget(case1 == this, parameters);
					if (parameters == null) {
						//this value doesn't matches we cannot match this case
						return null;
					}
				}
			}
			targets = targets.copyAndSetParams(parameters);
			if (statement != null) {
				return statement.matchTargets(targets, nextMatchers);
			}
			return nextMatchers.matchAllWith(targets);
		}
		@Override
		public void forEachParameterInfo(BiConsumer<ParameterInfo, Node> consumer) {
			SwitchNode.this.forEachParameterInfo(consumer);
		}
		@Override
		public <T> void generateTargets(Factory factory, ResultHolder<T> result, ParameterValueProvider parameters) {
			if (statement != null) {
				if (isCaseSelected(factory, parameters)) {
					statement.generateTargets(factory, result, parameters);
				}
			}
		}
		private boolean isCaseSelected(Factory factory, ParameterValueProvider parameters) {
			if (vrOfExpression == null) {
				return true;
			}
			Boolean value = vrOfExpression.generateTarget(factory, parameters, Boolean.class);
			return value == null ? false : value.booleanValue();
		}
	}
}
