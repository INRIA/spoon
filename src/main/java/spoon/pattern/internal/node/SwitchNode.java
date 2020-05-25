/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.node;

import spoon.SpoonException;
import spoon.pattern.internal.DefaultGenerator;
import spoon.pattern.internal.ResultHolder;
import spoon.pattern.internal.matcher.Matchers;
import spoon.pattern.internal.matcher.TobeMatched;
import spoon.pattern.internal.parameter.ParameterInfo;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.support.util.ImmutableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

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
public class SwitchNode extends AbstractNode implements InlineNode {

	private List<CaseNode> cases = new ArrayList<>();

	public SwitchNode() {
	}

	@Override
	public boolean replaceNode(RootNode oldNode, RootNode newNode) {
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
	public void addCase(PrimitiveMatcher vrOfExpression, RootNode statement) {
		cases.add(new CaseNode(vrOfExpression, statement));
	}

	@Override
	public <T> void generateTargets(DefaultGenerator generator, ResultHolder<T> result, ImmutableMap parameters) {
		for (CaseNode case1 : cases) {
			if (case1.isCaseSelected(generator, parameters)) {
				//generate result using first matching if branch
				generator.generateTargets(case1, result, parameters);
				return;
			}
		}
	}

	@Override
	public void forEachParameterInfo(BiConsumer<ParameterInfo, RootNode> consumer) {
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

	private class CaseNode extends AbstractNode implements InlineNode {
		/*
		 * is null for the default case
		 */
		private PrimitiveMatcher vrOfExpression;
		private RootNode statement;
		private CaseNode(PrimitiveMatcher vrOfExpression, RootNode statement) {
			this.vrOfExpression = vrOfExpression;
			this.statement = statement;
		}

		@Override
		public boolean replaceNode(RootNode oldNode, RootNode newNode) {
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
				return statement.replaceNode(oldNode, newNode);
			}
			return false;
		}

		@Override
		public TobeMatched matchTargets(TobeMatched targets, Matchers nextMatchers) {
			ImmutableMap parameters = targets.getParameters();
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
		public void forEachParameterInfo(BiConsumer<ParameterInfo, RootNode> consumer) {
			SwitchNode.this.forEachParameterInfo(consumer);
		}
		@Override
		public <T> void generateTargets(DefaultGenerator generator, ResultHolder<T> result, ImmutableMap parameters) {
			if (statement != null) {
				generator.generateTargets(statement, result, parameters);
			}
		}
		private boolean isCaseSelected(DefaultGenerator generator, ImmutableMap parameters) {
			if (vrOfExpression == null) {
				return true;
			}
			Boolean value = generator.generateSingleTarget(vrOfExpression, parameters, Boolean.class);
			return value == null ? false : value.booleanValue();
		}

		@Override
		public <T> void generateInlineTargets(DefaultGenerator generator, ResultHolder<T> result, ImmutableMap parameters) {
			Factory f = generator.getFactory();
			CoreFactory cf = f.Core();
			CtBlock<?> block = cf.createBlock();
			if (statement != null) {
				block.setStatements(generator.generateTargets(statement, parameters, CtStatement.class));
			}
			if (vrOfExpression != null) {
				//There is if expression
				CtIf ifStmt = cf.createIf();
				ifStmt.setCondition(generator.generateSingleTarget(vrOfExpression, parameters, CtExpression.class));
				ifStmt.setThenStatement(block);
				result.addResult((T) ifStmt);
			} else {
				//There is no expression. It represents the last else block
				result.addResult((T) block);
			}
		}
	}

	@Override
	public <T> void generateInlineTargets(DefaultGenerator generator, ResultHolder<T> result, ImmutableMap parameters) {
		CtStatement resultStmt = null;
		CtStatement lastElse = null;
		CtIf lastIf = null;
		for (CaseNode caseNode : cases) {
			CtStatement stmt = generator.generateSingleTarget(caseNode, parameters, CtStatement.class);
			if (stmt instanceof CtIf) {
				CtIf ifStmt = (CtIf) stmt;
				if (lastIf == null) {
					//it is first IF
					resultStmt = ifStmt;
					lastIf = ifStmt;
				} else {
					//it is next IF. Append it as else into last IF
					lastIf.setElseStatement(ifStmt);
					lastIf = ifStmt;
				}
			} else {
				if (lastElse != null) {
					throw new SpoonException("Only one SwitchNode can have no expression.");
				}
				lastElse = stmt;
			}
		}
		if (lastIf == null) {
			//there is no IF
			if (lastElse != null) {
				result.addResult((T) lastElse);
			}
			return;
		}
		if (lastElse != null) {
			//append last else into lastIf
			lastIf.setElseStatement(lastElse);
		}
		result.addResult((T) resultStmt);
	}
}
