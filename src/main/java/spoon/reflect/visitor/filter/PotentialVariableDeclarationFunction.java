/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CaseKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCFlowBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLabelledFlowBreak;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSwitchExpression;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.chain.CtQueryAware;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This mapping function searches for all {@link CtVariable} instances,
 * which might be a declaration of an input {@link CtElement}.
 * <br>
 * It can be used to search for variable declarations of
 * variable references and for detection of variable name conflicts
 * <br>
 * It returns {@link CtLocalVariable} instances,
 * or it returns {@link CtCatchVariable} instances of catch blocks,
 * or i returns {@link CtParameter} instances of methods, lambdas and catch blocks.
 * or it returns {@link CtField} instances from wrapping classes and their super classes too.
 * <br>
 * The elements are visited in the following order: first elements are thought in the nearest parent blocks,
 * then in the fields of wrapping classes, then in the fields of super classes, etc.
 * <br>
 * Example: Search for all potential {@link CtVariable} declarations<br>
 * <pre> {@code
 * CtVariableReference varRef = ...;
 * varRef.map(new PotentialVariableDeclarationFunction()).forEach(...process result...);
 * }
 * </pre>
 * Example: Search for {@link CtVariable} declaration of variable named `varName` in scope "scope"
 * <pre> {@code
 * CtElement scope = ...;
 * String varName = "anVariableName";
 * CtVariable varOrNull = scope.map(new PotentialVariableDeclarationFunction(varName)).first();
 * }
 * </pre>
 */
public class PotentialVariableDeclarationFunction implements CtConsumableFunction<CtElement>, CtQueryAware {

	private boolean isTypeOnTheWay;
	private final String variableName;
	private CtQuery query;
	private boolean isInStaticScope;

	public PotentialVariableDeclarationFunction() {
		this.variableName = null;
	}

	/**
	 * Searches for a variable with exact name.
	 * @param variableName
	 */
	public PotentialVariableDeclarationFunction(String variableName) {
		this.variableName = variableName;
	}

	@Override
	public void apply(CtElement input, CtConsumer<Object> outputConsumer) {
		isTypeOnTheWay = false;
		isInStaticScope = false;
		//Search previous siblings for element which may represents the declaration of this local variable
		CtQuery siblingsQuery = input.getFactory().createQuery()
				.map(new SiblingsFunction().mode(SiblingsFunction.Mode.PREVIOUS))
				//select only CtVariable nodes
				.select(new TypeFilter<>(CtVariable.class));
		if (variableName != null) {
			//variable name is defined so we have to search only for variables with that name
			siblingsQuery = siblingsQuery.select(new NamedElementFilter<>(CtNamedElement.class, variableName));
		}

		Set<Scope> scopes = new LinkedHashSet<>();
		CtElement scopeElement = input;
		//Search input and then all parents until first CtPackage for element which may represents the declaration of this local variable
		while (scopeElement != null && !(scopeElement instanceof CtPackage) && scopeElement.isParentInitialized()) {
			CtElement parent = scopeElement.getParent();
			if (parent instanceof CtType<?>) {
				isTypeOnTheWay = true;
				//visit each CtField of `parent` CtType
				CtQuery q = parent.map(new AllTypeMembersFunction(CtField.class));
				q.forEach((CtField<?> field) -> {
					if (isInStaticScope && !field.hasModifier(ModifierKind.STATIC)) {
						/*
						 * the variable reference is used in static scope,
						 * but the field is not static - ignore it
						 */
						return;
					}
					//else send field as potential variable declaration
					if (sendToOutput(field, outputConsumer)) {
						//and terminate the internal query q if outer query is already terminated
						q.terminate();
					}
				});
				if (query.isTerminated()) {
					return;
				}
			} else if (parent instanceof CtSwitch && scopeElement instanceof CtCase<?> caseElement) {
				if (caseElement.getCaseKind() == CaseKind.COLON) {
					SiblingsFunction siblingsFunction = new SiblingsFunction().mode(SiblingsFunction.Mode.PREVIOUS);
					List<CtCase<?>> list = input.getFactory().createQuery()
							.map(siblingsFunction)
							.setInput(scopeElement)
							.filterChildren(new TypeFilter<>(CtCase.class))
							.list();

					for (CtCase<?> c : list) {
						for (CtStatement statement : c.getStatements()) {
							if (statement instanceof CtLocalVariable && ((CtLocalVariable<?>) statement).getSimpleName().equals(variableName)) {
								sendToOutput((CtVariable<?>) statement, outputConsumer);
								return;
							}
						}
					}
				}
			} else if (parent instanceof CtBodyHolder || parent instanceof CtStatementList || parent instanceof CtExpression<?>) {
				//visit all previous CtVariable siblings of scopeElement element in parent BodyHolder or Statement list
				siblingsQuery.setInput(scopeElement).forEach(outputConsumer);
				if (query.isTerminated()) {
					return;
				}

				// visit parameters of CtCatch and CtExecutable (method, lambda)
				if (parent instanceof CtCatch ctCatch) {
					if (sendToOutput(ctCatch.getParameter(), outputConsumer)) {
						return;
					}
				} else if (parent instanceof CtExecutable<?> exec) {
					for (CtParameter<?> param : exec.getParameters()) {
						if (sendToOutput(param, outputConsumer)) {
							return;
						}
					}
				}

				// TODO: Does the code resolve a for loop variable? e.g. for (int i = 0; ...) - if not, this should be added here as well.
			}

			scopes = updateChildScopesForParent(scopes, scopeElement, parent);

			for (var scope : scopes) {
				if (scope.appliesTo(scopeElement) && sendToOutput(scope.ctLocalVariable, outputConsumer)) {
					return;
				}
			}

			if (parent instanceof CtModifiable) {
				isInStaticScope = isInStaticScope || ((CtModifiable) parent).hasModifier(ModifierKind.STATIC);
			}
			scopeElement = parent;
		}
	}

	private record Scope(CtLocalVariable<?> ctLocalVariable, CtElement ctElement, boolean matches) {
		public Scope with(CtElement ctElement, boolean matches) {
			return new Scope(ctLocalVariable, ctElement, matches);
		}

		public boolean appliesTo(CtElement ctElement) {
			return ctElement == this.ctElement || ctElement.hasParent(this.ctElement);
		}

		@Override
		public String toString() {
			return "Scope['%s' @ %d, '%s' @ %d, matches=%s]".formatted(
				ctLocalVariable,
				System.identityHashCode(ctLocalVariable),
				ctElement,
				System.identityHashCode(ctElement),
				matches
			);
		}
	}


	private static Set<Scope> exploreBranchForNewScopes(CtElement branch) {
		Set<Scope> result = new LinkedHashSet<>();

		// Any type pattern is okay, the sibling branches will be explored by the updateChildScopesForParent.
		CtElement child = branch.filterChildren(new TypeFilter<>(CtTypePattern.class)).first();
		while (child != null && child != branch) {
			// The children should always have a parent, given that they are children of the branch.
			CtElement parent = child.getParent();
			result = updateChildScopesForParent(result, child, parent);
			child = parent;
		}

		return result.stream().filter(scope -> scope.ctElement == branch).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	private static boolean completesNormally(CtStatement statement) {
		// FIXME: The JLS has a definition for what "cannot complete normally" is
		return !(statement instanceof CtStatementList ctStatementList
			&& !ctStatementList.getStatements().isEmpty()
			&& ctStatementList.getLastStatement() instanceof CtCFlowBreak);
	}

	private static boolean hasNoReachableBreakWithTarget(CtStatement statement, CtStatement breakTarget) {
		// FIXME: This does not check whether the break statement is actually reachable
		return statement
			.filterChildren(new TypeFilter<>(CtBreak.class))
			.filterChildren((CtLabelledFlowBreak ctBreak) -> ctBreak.getLabelledStatement() == breakTarget)
			.first() == null;
	}

	/**
	 * Updates the child scopes for the parent by applying the JLS rules for pattern variable scopes.
	 *
	 * @param childScopes the scopes that have been found in the child, on the first call pass an empty set.
	 * @param child       the child for which the scopes have been found
	 * @param parent      the parent of the passed child, this is passed separately in case the child is not initialized with the parent
	 * @return the scopes that apply to the parent
	 */
	private static Set<Scope> updateChildScopesForParent(Collection<Scope> childScopes, CtElement child, CtElement parent) {
		Set<Scope> filteredChildScopes = childScopes.stream()
			.filter(scope -> scope.ctElement == child)
			.collect(Collectors.toCollection(LinkedHashSet::new));

		// TODO: Later remove for performance optimization?
		if (child.isParentInitialized() && !child.hasParent(parent)) {
			throw new IllegalStateException("The parent '%s' does not seem to be a parent of the child '%s'".formatted(parent, child));
		}

		// TODO: Add missing expression/statements, mainly a ? b : c and switch expressions/switch statements


		if (parent instanceof CtStatementList ctStatementList) {
			Set<Scope> result = new LinkedHashSet<>();

			// The following rule applies to a block statement S contained in a block that is not a switch block:
			// - A pattern variable introduced by S is definitely matched at all the block statements following S,
			//   if any, in the block.

			List<CtStatement> previousSiblings = ctStatementList.getFactory().createQuery()
				.map(new SiblingsFunction().mode(SiblingsFunction.Mode.PREVIOUS))
				.select(new TypeFilter<>(CtStatement.class))
				.setInput(child)
				.list();

			for (CtStatement previousSibling : previousSiblings) {
				for (var scope : exploreBranchForNewScopes(previousSibling)) {
					result.add(scope.with(child, scope.matches()));
				}
			}

			return result;
		}

		if (parent instanceof CtBinaryOperator<?> operator && (operator.getKind() == BinaryOperatorKind.AND
			|| operator.getKind() == BinaryOperatorKind.OR)) {
			// In `a <op> b`, both a and b can introduce pattern variables, but the passed child scopes will be for only one of the
			// branches. This will add the other branch's scopes as well:
			CtElement otherBranch =
				operator.getLeftHandOperand() == child ? operator.getRightHandOperand() : operator.getLeftHandOperand();
			filteredChildScopes.addAll(exploreBranchForNewScopes(otherBranch));


			// A pattern variable is introduced by a && b when true iff either
			// -  (i) it is introduced by a when true or
			// - (ii) it is introduced by b when true.
			//
			// A pattern variable is introduced by a || b when false iff either
			// -  (i) it is introduced by a when false or
			// - (ii) it is introduced by b when false.
			//
			// The difference between && and || is that for && it must match true,
			// and for || it must match false. The operator.getKind() == BinaryOperatorKind.AND is true
			// when it is an &&, and false when it is an ||, which matches the above rules.
			//
			// The **either** part is only relevant for code that does not compile, which is assumed to not
			// be the case here?
			return filteredChildScopes.stream()
				.filter(scope -> scope.matches() == (operator.getKind() == BinaryOperatorKind.AND))
				.map(scope -> scope.with(operator, scope.matches()))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		}

		if (parent instanceof CtUnaryOperator<?> operator && operator.getKind() == UnaryOperatorKind.NOT) {
			// TODO: Later remove for performance optimization?
			if (operator.getOperand() != child) {
				throw new IllegalStateException("Unknown child for unary operator: " + child);
			}

			// For !(expr) the following holds:
			// - If a pattern variable is introduced by expr when true, then it is available for matching when false.
			// - If a pattern variable is introduced by expr when false, then it is available for matching when true.

			return filteredChildScopes.stream()
				// swap the matches, because the operator is a NOT, matchesTrue will become matchesFalse and vice versa
				.map(scope -> scope.with(operator, !scope.matches))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		}

		if (parent instanceof CtBinaryOperator<?> operator && operator.getKind() == BinaryOperatorKind.INSTANCEOF) {
			Set<Scope> result = new LinkedHashSet<>();
			// This handles both regular type patterns and record patterns that could introduce pattern variables too
			for (var ctTypePattern : operator.getRightHandOperand().getElements(new TypeFilter<>(CtTypePattern.class))) {
				result.add(new Scope(ctTypePattern.getVariable(), operator, true));
			}

			return result;
		}

		if (parent instanceof CtSwitchExpression<?,?> ctSwitchExpression) {
			// The following rule applies to a switch expression with a switch block consisting of switch rules:
			//
 			// A pattern variable introduced by a switch label is definitely matched in the associated switch rule
			// expression, switch rule block, or switch rule throw statement.
			throw new IllegalStateException("To be implemented");
		}

		// The child of a switch statement can only be
		// - the selector, which cannot introduce pattern variables according to the JLS, or
		// - a case, which can introduce pattern variables
		if (parent instanceof CtSwitch<?> ctSwitch && child instanceof CtCase<?> ctCase) {
			throw new IllegalStateException("To be implemented");
		}

		// An if without a then statement `if (a);` can introduce pattern variables, but they can only be referenced from the
		// then, which does not exist -> these can be ignored.
		if (parent instanceof CtIf ctIf && ctIf.getThenStatement() != null) {
			Set<Scope> result = new LinkedHashSet<>();
			// The child could be:
			// - the condition (can introduce scopes)
			// - the then branch
			// - the else branch
			//
			// According to the JLS scopes can only be introduced by the condition
			// -> scopes from the then/else branch should be discarded
			if (ctIf.getCondition() != child) {
				filteredChildScopes = exploreBranchForNewScopes(ctIf.getCondition());
			}

			boolean canThenComplete = completesNormally(ctIf.getThenStatement());
			for (Scope scope : filteredChildScopes) {
				// For an `if (e) S` (with maybe an else), a pattern variable introduced by `e` when `true` is definitely matched at `S`.
				if (scope.matches) {
					result.add(scope.with(ctIf.getThenStatement(), true));
				}

				// A pattern variable is introduced by `if (e) S` iff
				// -  (i) it is introduced by e when false and
				// - (ii) S cannot complete normally.
				if (ctIf.getElseStatement() == null && !scope.matches && !canThenComplete) {
					result.add(scope.with(ctIf, false));
				}

				// The following rules apply to a statement `if (e) S else T`:
				if (ctIf.getElseStatement() != null) {
					// A pattern variable introduced by e when false is definitely matched at T.
					if (!scope.matches) {
						result.add(scope.with(ctIf.getElseStatement(), false));
					}

					// A pattern variable is introduced by if (e) S else T iff either:
					// - It is introduced by e when true, and S can complete normally, and T cannot complete normally; or
					boolean canElseComplete = completesNormally(ctIf.getElseStatement());
					if (scope.matches && canThenComplete && !canElseComplete) {
						result.add(scope.with(ctIf, true));
					}

					// - It is introduced by e when false, and S cannot complete normally, and T can complete normally.
					if (!scope.matches && !canThenComplete && canElseComplete) {
						result.add(scope.with(ctIf, false));
					}
				}
			}

			return result;
		}

		if (parent instanceof CtWhile ctWhile) {
			Set<Scope> result = new LinkedHashSet<>();
			if (child != ctWhile.getLoopingExpression()) {
				filteredChildScopes = exploreBranchForNewScopes(ctWhile.getLoopingExpression());
			}

			for (Scope scope : filteredChildScopes) {
				if (scope.ctElement != ctWhile.getLoopingExpression()) {
					throw new IllegalStateException("Found an invalid scope for while statement: " + scope);
				}

				// The following rules apply to a statement `while (e) S`:

				// A pattern variable introduced by e when true is definitely matched at S.
				if (scope.matches) {
					result.add(scope.with(ctWhile.getBody(), true));
				}

				// A pattern variable is introduced by while (e) S iff
				// - (i) it is introduced by e when false and
				// - (ii) S does not contain a reachable break statement for which the while
				//        statement is the break target
				if (!scope.matches && hasNoReachableBreakWithTarget(ctWhile.getBody(), ctWhile)) {
					result.add(scope.with(ctWhile, false));
				}
			}

			return result;
		}

		if (parent instanceof CtDo ctDo) {
			Set<Scope> result = new LinkedHashSet<>();
			if (child != ctDo.getLoopingExpression()) {
				filteredChildScopes = exploreBranchForNewScopes(ctDo.getLoopingExpression());
			}

			// TODO: This is how it should work:
			/*do { System.out.println(); } while (!(scopeElement instanceof CtDo ctElement)); System.out.println(ctElement);*/

			// The following rule applies to a statement `do S while (e)`:
			// - A pattern variable is introduced by do S while (e) iff
			//   - (i) it is introduced by e when false and
			//   - (ii) S does not contain a reachable break statement
			//          for which the do statement is the break target.
			for (Scope scope : filteredChildScopes) {
				if (scope.ctElement != ctDo.getLoopingExpression()) {
					throw new IllegalStateException("Found an invalid scope for while statement: " + scope);
				}

				if (!scope.matches && hasNoReachableBreakWithTarget(ctDo.getBody(), ctDo)) {
					result.add(scope.with(ctDo, false));
				}
			}

			return result;
		}

		if (parent instanceof CtFor ctFor) {
			Set<Scope> result = new LinkedHashSet<>();
			if (child != ctFor.getExpression()) {
				filteredChildScopes = exploreBranchForNewScopes(ctFor.getExpression());
			}

			// The following rules apply to a basic for statement (§14.14.1):
			for (Scope scope : filteredChildScopes) {
				if (scope.ctElement != ctFor.getExpression()) {
					throw new IllegalStateException("Found an invalid scope for for statement: " + scope);
				}

				// - A pattern variable introduced by the condition expression when true is definitely
				//   matched at both the incrementation part and the contained statement.
				if (scope.matches) {
					for (var update : ctFor.getForUpdate()) {
						result.add(scope.with(update, true));
					}

					result.add(scope.with(ctFor.getBody(), true));
				}

				// - A pattern variable is introduced by a basic for statement iff
				//   - (i) it is introduced by the condition expression when false and
				//   - (ii) the contained statement, S, does not contain a reachable break for which the
				//          basic for statement is the break target.
				//
				// An enhanced for statement (§14.14.2) is defined by translation to a basic for statement, so no special rules need to be provided for it.
				// TODO: Handle enhanced for statement translation, and figure out how one could instanceof pattern match in an enhanced for?
				if (!scope.matches && hasNoReachableBreakWithTarget(ctFor.getBody(), ctFor)) {
					result.add(scope.with(ctFor, false));
				}
			}

			return result;
		}

		return Set.of();
	}

	/**
	 * @param var
	 * @param output
	 * @return true if query processing is terminated
	 */
	private boolean sendToOutput(CtVariable<?> var, CtConsumer<Object> output) {
		if (variableName == null || variableName.equals(var.getSimpleName())) {
			output.accept(var);
		}
		return query.isTerminated();
	}

	/**
	 * This method provides access to current state of this function.
	 * It is intended to be called by other mapping functions at query processing time or after query is finished.
	 *
	 * @return true if there is an local class on the way from the input of this mapping function
	 * to the actually found potential variable declaration
	 */
	public boolean isTypeOnTheWay() {
		return isTypeOnTheWay;
	}

	@Override
	public void setQuery(CtQuery query) {
		this.query = query;
	}
}
