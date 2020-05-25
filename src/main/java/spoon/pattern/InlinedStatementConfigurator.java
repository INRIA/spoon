/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern;

import static spoon.pattern.PatternBuilder.bodyToStatements;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import spoon.SpoonException;
import spoon.pattern.internal.node.ForEachNode;
import spoon.pattern.internal.node.ListOfNodes;
import spoon.pattern.internal.node.ParameterNode;
import spoon.pattern.internal.node.PrimitiveMatcher;
import spoon.pattern.internal.node.RootNode;
import spoon.pattern.internal.node.SwitchNode;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtAbstractVisitor;
import spoon.support.Experimental;

/**
 * Builds inline statements of Pattern
 *
 * For example if the `for` statement in this pattern model
 * <pre><code>
 * for(Object x : $iterable$) {
 *	System.out.println(x);
 * }
 * </code></pre>
 * is configured as inline statement and a Pattern is substituted
 * using parameter <code>$iterable$ = new String[]{"A", "B", "C"}</code>
 * then pattern generated this code
 * <pre><code>
 * System.out.println("A");
 * System.out.println("B");
 * System.out.println("C");
 * </code></pre>
 * because inline statements are executed during substitution process and are not included in generated result.
 *
 * Main documentation at http://spoon.gforge.inria.fr/pattern.html.
 */
@Experimental
public class InlinedStatementConfigurator {

	private final PatternBuilder patternBuilder;
	private boolean failOnMissingParameter = true;
	private ConflictResolutionMode conflictResolutionMode = ConflictResolutionMode.FAIL;

	public InlinedStatementConfigurator(PatternBuilder patternBuilder) {
		this.patternBuilder = patternBuilder;
	}

	/**
	 * @return current {@link ConflictResolutionMode}
	 */
	public ConflictResolutionMode getConflictResolutionMode() {
		return conflictResolutionMode;
	}

	/**
	 * Defines what happens when before explicitly added {@link RootNode} has to be replaced by another {@link RootNode}
	 * @param conflictResolutionMode to be applied mode
	 * @return this to support fluent API
	 */
	public InlinedStatementConfigurator setConflictResolutionMode(ConflictResolutionMode conflictResolutionMode) {
		this.conflictResolutionMode = conflictResolutionMode;
		return this;
	}

	/**
	 * marks all CtIf and CtForEach whose expression contains a variable reference named `variableName` as inline statement.
	 * @param variableName to be searched variable name
	 * @return this to support fluent API
	 */
	public InlinedStatementConfigurator inlineIfOrForeachReferringTo(String variableName) {
		patternBuilder.patternQuery
			.filterChildren((CtVariableReference varRef) -> variableName.equals(varRef.getSimpleName()))
			.forEach(this::byElement);
		return this;
	}

	/**
	 * marks all CtIf and CtForEach whose expression contains element as inline statement.
	 * @param element a child of CtIf or CtForEach
	 * @return this to support fluent API
	 */
	InlinedStatementConfigurator byElement(CtElement element) {
		CtStatement stmt = element instanceof CtStatement ? (CtStatement) element : element.getParent(CtStatement.class);
		//called for first parent statement of all current parameter substitutions
		stmt.accept(new CtAbstractVisitor() {
			@Override
			public void visitCtForEach(CtForEach foreach) {
				markAsInlined(foreach);
			}
			@Override
			public void visitCtIf(CtIf ifElement) {
				markAsInlined(ifElement);
			}
		});
		return this;
	}

	/**
	 * marks {@link CtForEach} as inline statement.
	 * @param foreach to be marked {@link CtForEach} element
	 * @return this to support fluent API
	 */
	public InlinedStatementConfigurator markAsInlined(CtForEach foreach) {
		//detect meta elements by different way - e.g. comments?
		RootNode vr = patternBuilder.getPatternNode(foreach.getExpression());
		if ((vr instanceof PrimitiveMatcher) == false) {
			throw new SpoonException("Each inline `for(x : iterable)` statement must have defined pattern parameter for `iterable` expression");
		}
		PrimitiveMatcher parameterOfExpression = (PrimitiveMatcher) vr;
		ForEachNode mvr = new ForEachNode();
		mvr.setIterableParameter(parameterOfExpression);
		CtLocalVariable<?> lv = foreach.getVariable();
		//create locally unique name of this local parameter
		String paramName = lv.getSimpleName();
		patternBuilder.configureLocalParameters(pb -> {
			pb.parameter(paramName).byVariable(lv);
			mvr.setLocalParameter(pb.getCurrentParameter());
		});
		mvr.setNestedModel(patternBuilder.getPatternNode(foreach, CtRole.BODY, CtRole.STATEMENT));
		/*
		 * create Substitution request for whole `foreach`,
		 * resolve the expressions at substitution time
		 * and substitute the body of `foreach` as subpattern
		 * 0 or more times - once for each value of Iterable expression.
		 */
		patternBuilder.setNodeOfElement(foreach, mvr, conflictResolutionMode);
		return this;
	}

	/**
	 * marks {@link CtIf} as inline statement.
	 * @param ifElement to be marked {@link CtIf} element
	 * @return this to support fluent API
	 */
	public InlinedStatementConfigurator markAsInlined(CtIf ifElement) {
		SwitchNode osp = new SwitchNode();
		boolean[] canBeInline = { true };
		forEachIfCase(ifElement, (expression, block) -> {
			//detect meta elements by different way - e.g. comments?
			if (expression != null) {
				//expression is not null, it is: if(expression) {}
				RootNode vrOfExpression = patternBuilder.getPatternNode(expression);
				if (vrOfExpression instanceof ParameterNode == false) {
					if (failOnMissingParameter) {
						throw new SpoonException("Each inline `if` statement must have defined pattern parameter in expression. If you want to ignore this, then call InlinedStatementConfigurator#setFailOnMissingParameter(false) first.");
					} else {
						canBeInline[0] = false;
						return;
					}
				}
				if (vrOfExpression instanceof PrimitiveMatcher) {
					osp.addCase((PrimitiveMatcher) vrOfExpression, getPatternNode(bodyToStatements(block)));
				} else {
					throw new SpoonException("Inline `if` statement have defined single value pattern parameter in expression. But there is " + vrOfExpression.getClass().getName());
				}
			} else {
				//expression is null, it is: else {}
				osp.addCase(null, getPatternNode(bodyToStatements(block)));
			}
		});
		if (canBeInline[0]) {
			/*
			 * create Substitution request for whole `if`,
			 * resolve the expressions at substitution time and substitute only the `if` then/else statements, not `if` itself.
			 */
			patternBuilder.setNodeOfElement(ifElement, osp, conflictResolutionMode);
		}
		return this;
	}

	private ListOfNodes getPatternNode(List<? extends CtElement> template) {
		List<RootNode> nodes = new ArrayList<>(template.size());
		for (CtElement element : template) {
			nodes.add(patternBuilder.getPatternNode(element));
		}
		return new ListOfNodes(nodes);
	}

	/**
	 * calls function once for each expression/then block and at the end calls function for last else block.
	 *
	 * @param ifElement
	 * @param consumer
	 * @return true if all function calls returns true or if there is no function call
	 */
	private void forEachIfCase(CtIf ifElement, BiConsumer<CtExpression<Boolean>, CtStatement> consumer) {
		consumer.accept(ifElement.getCondition(), ifElement.getThenStatement());
		CtStatement elseStmt = getElseIfStatement(ifElement.getElseStatement());
		if (elseStmt instanceof CtIf) {
			//another else if case
			forEachIfCase((CtIf) elseStmt, consumer);
		} else if (elseStmt != null) {
			//last else
			consumer.accept(null, elseStmt);
		}
	}

	private CtStatement getElseIfStatement(CtStatement elseStmt) {
		if (elseStmt instanceof CtBlock<?>) {
			CtBlock<?> block = (CtBlock<?>) elseStmt;
			if (block.isImplicit()) {
				List<CtStatement> stmts = block.getStatements();
				if (stmts.size() == 1) {
					if (stmts.get(0) instanceof CtIf) {
						return stmts.get(0);
					}
				}
			}
		}
		return elseStmt;
	}

	public boolean isFailOnMissingParameter() {
		return failOnMissingParameter;
	}

	/**
	 * @param failOnMissingParameter set true if it should fail when some statement cannot be handled as inline
	 * set false if ssuch statement should be kept as part of template.
	 * @return this to support fluent API
	 */
	public InlinedStatementConfigurator setFailOnMissingParameter(boolean failOnMissingParameter) {
		this.failOnMissingParameter = failOnMissingParameter;
		return this;
	}
}
