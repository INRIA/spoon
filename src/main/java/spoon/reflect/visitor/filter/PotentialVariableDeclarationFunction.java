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
import spoon.reflect.code.CtAbstractSwitch;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
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
	 *
	 * @param variableName
	 */
	public PotentialVariableDeclarationFunction(String variableName) {
		this.variableName = variableName;
	}

	@Override
	public void apply(CtElement input, CtConsumer<Object> outputConsumer) {
		isTypeOnTheWay = false;
		isInStaticScope = false;

		List<Scope> scopes = new ArrayList<>();
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
			}

			scopes = updateChildScopesForParent(scopes, scopeElement, parent);

			for (var scope : scopes) {
				if (scope.appliesTo(scopeElement) && sendToOutput(scope.ctVariable(), outputConsumer)) {
					return;
				}
			}

			if (parent instanceof CtModifiable) {
				isInStaticScope = isInStaticScope || ((CtModifiable) parent).hasModifier(ModifierKind.STATIC);
			}
			scopeElement = parent;
		}
	}

	private interface Scope {
		CtVariable<?> ctVariable();

		CtElement ctElement();

		default boolean appliesTo(CtElement ctElement) {
			return ctElement == this.ctElement() || ctElement.hasParent(this.ctElement());
		}
	}

	/**
	 * Represents the scope of a local pattern variable.
	 *
	 * @param ctVariable the local variable
	 * @param ctElement  the element in which the variable can be referenced
	 * @param matches    for pattern variables whether the variable matches when true or false, for other variables this should be empty
	 */
	private record PatternScope(CtVariable<?> ctVariable, CtElement ctElement, boolean matches) implements Scope {
		public Scope with(CtElement ctElement, boolean matches) {
			return new PatternScope(ctVariable, ctElement, matches);
		}

		@Override
		public String toString() {
			return "PatternScope['%s' @ %d, '%s' @ %d, matches=%s]".formatted(
				ctVariable,
				System.identityHashCode(ctVariable),
				ctElement,
				System.identityHashCode(ctElement),
				matches
			);
		}
	}

	private record VariableScope(CtVariable<?> ctVariable, CtElement ctElement) implements Scope {
	}

	// This searches for new scopes introduced by the branch and returns the ones that apply to the branch
	private static List<Scope> exploreBranchForNewScopes(CtElement branch) {
		List<Scope> result = new ArrayList<>();

		// Any type pattern is okay, the sibling branches will be explored by the updateChildScopesForParent.
		CtElement child = branch.filterChildren(new TypeFilter<>(CtVariable.class)).first();
		while (child != null && child != branch) {
			// The children should always have a parent, given that they are children of the branch.
			CtElement parent = child.getParent();
			result = updateChildScopesForParent(result, child, parent);
			child = parent;
		}

		// If the branch itself is a variable declaration, then this variable is in scope for the branch
		// -> has to be added to the result.
		if (child == branch && child instanceof CtVariable<?> ctVariable) {
			result.add(new VariableScope(ctVariable, ctVariable));
		}

		return result.stream().filter(scope -> scope.ctElement() == branch).toList();
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
	 * @param childScopes the scopes that have been found in the child, on the first call pass an empty collection.
	 * @param child       the child for which the scopes have been found
	 * @param parent      the parent of the passed child, this is passed separately in case the child is not initialized with the parent
	 * @return the scopes that apply to the parent
	 */
	private static List<Scope> updateChildScopesForParent(Collection<? extends Scope> childScopes, CtElement child, CtElement parent) {
		List<Scope> filteredChildScopes = childScopes.stream()
			// only keep the scopes that were valid for the child, the ones where the scope was less than the child
			// can not escape the child, so they can be discarded
			.filter(scope -> scope.ctElement() == child)
			.collect(Collectors.toCollection(ArrayList::new));

		// TODO: Add missing expression/statements, mainly a ? b : c and switch expressions/switch statements

		// TODO: What about the filteredChildScopes?
		// TODO: CtBlock or CtStatementList?
		if (parent instanceof CtBlock<?> ctStatementList) {
			List<Scope> result = new ArrayList<>();

			List<CtStatement> previousSiblings = ctStatementList.getFactory().createQuery()
				.map(new SiblingsFunction().mode(SiblingsFunction.Mode.PREVIOUS))
				.select(new TypeFilter<>(CtStatement.class))
				.setInput(child)
				.list();

			for (CtStatement previousSibling : previousSiblings) {
				for (var scope : exploreBranchForNewScopes(previousSibling)) {
					// Any variable introduced by a previous sibling will be in scope for the current statement.
					//
					// For pattern variables the JLS states this:
					//
					// The following rule applies to a block statement S contained in a block that is not a switch block:
					// - A pattern variable introduced by S is definitely matched at all the block statements following S,
					//   if any, in the block.
					result.add(new VariableScope(scope.ctVariable(), child));
				}
			}

			return result;
		}

		// TODO: Go through the JLS rules for scoping to make sure everything works correctly...
		if (parent instanceof CtType<?> ctType) {
			List<Scope> result = new ArrayList<>();

			for (var field : parent.getFactory().createQuery(ctType)
				.map(new AllTypeMembersFunction(CtField.class))
				.list(CtVariable.class)) {
				result.add(new VariableScope(field, ctType));
			}

			return result;
		}


		// visit parameters of CtCatch and CtExecutable (method, lambda)
		if (parent instanceof CtCatch ctCatch) {
			return List.of(new VariableScope(ctCatch.getParameter(), ctCatch));
		}

		// TODO: Something is extremely slow with the current impl

		if (parent instanceof CtExecutable<?> exec) {
			List<Scope> result = new ArrayList<>();

			for (CtParameter<?> param : exec.getParameters()) {
				result.add(new VariableScope(param, exec.getBody()));
			}

			return result;
		}

		// The following rule applies to a switch expression with a switch block consisting of switch rules:
		//
		// A pattern variable introduced by a switch label is definitely matched in the associated switch rule
		// expression, switch rule block, or switch rule throw statement.
		//
		// The following rules apply to a switch expression with a switch block consisting of switch labeled statement groups (§14.11.1):
		//
		// A pattern variable introduced by a switch label is definitely matched in all the statements of the associated switch
		// labeled statement group.
		//
		// A pattern variable introduced by a statement S contained in a switch labeled statement group is definitely matched at all the
		// statements following S, if any, in the switch labeled statement group.

		if (parent instanceof CtAbstractSwitch<?> ctAbstractSwitch &&
			ctAbstractSwitch.getCases().stream().anyMatch(ctCase -> ctCase == child)) {
			List<Scope> result = new ArrayList<>();

			// TODO: Does this apply to expressions as well?

			// In a switch statement the previous siblings expose the variables to the next siblings:
			List<CtCase<?>> previousSiblings = parent.getFactory().createQuery()
				.map(new SiblingsFunction().mode(SiblingsFunction.Mode.PREVIOUS))
				.select(new TypeFilter<>(CtCase.class))
				.setInput(child)
				.list();

			for (CtCase<?> previousSibling : previousSiblings) {
				for (var scope : exploreBranchForNewScopes(previousSibling)) {
					// Any variable introduced by a previous sibling will be in scope for the current statement.
					//
					// For pattern variables the JLS states this:
					//
					// The following rule applies to a switch statement S with switch block B:
					// - A pattern variable introduced by a switch rule is definitely matched at all the switch rules following it, if any, in B.
					result.add(new VariableScope(scope.ctVariable(), child));
				}
			}

			return result;
		}

		if (parent instanceof CtCase<?> ctCase) {
			// TODO: Make sure it resolves correctly for guard expressions

			// All variables defined in the case expressions will be in scope for the case
			List<Scope> result = ctCase.getCaseExpressions()
				.stream()
				.flatMap(expr -> expr.getElements(new TypeFilter<>(CtTypePattern.class)).stream())
				.map(ctTypePattern -> (Scope) new VariableScope(ctTypePattern.getVariable(), ctCase))
				.collect(Collectors.toCollection(ArrayList::new));

			for (CtStatement ctStatement : ctCase.getStatements()) {
				for (var scope : exploreBranchForNewScopes(ctStatement)) {
					result.add(new VariableScope(scope.ctVariable(), parent));
				}
			}

			return result;
		}

		if (parent instanceof CtSwitch<?> && child instanceof CtCase<?> caseElement) {
			if (caseElement.getCaseKind() == CaseKind.COLON) { // TODO: This does not behave right with blocks...
				return parent.getFactory().createQuery()
					.map(new SiblingsFunction().mode(SiblingsFunction.Mode.PREVIOUS))
					.setInput(caseElement)
					.filterChildren(new TypeFilter<>(CtCase.class))
					.list(CtCase.class)
					.stream()
					.flatMap(ctCase -> exploreBranchForNewScopes(ctCase).stream())
					.map(scope -> (Scope) new VariableScope(scope.ctVariable(), caseElement))
					.toList();
			}
		}

		// An if without a then statement `if (a);` can introduce pattern variables, but they can only be referenced from the
		// then, which does not exist -> these can be ignored.
		if (parent instanceof CtIf ctIf && ctIf.getThenStatement() != null) {
			List<Scope> result = new ArrayList<>();
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
			for (PatternScope scope : filteredChildScopes.stream().filter(scope -> scope instanceof PatternScope)
				.map(scope -> (PatternScope) scope).toList()) {
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
			List<Scope> result = new ArrayList<>();
			if (child != ctWhile.getLoopingExpression()) {
				filteredChildScopes = exploreBranchForNewScopes(ctWhile.getLoopingExpression());
			}

			for (PatternScope scope : filteredChildScopes.stream().filter(scope -> scope instanceof PatternScope)
				.map(scope -> (PatternScope) scope).toList()) {
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
			List<Scope> result = new ArrayList<>();
			if (child != ctDo.getLoopingExpression()) {
				filteredChildScopes = exploreBranchForNewScopes(ctDo.getLoopingExpression());
			}

			// The following rule applies to a statement `do S while (e)`:
			// - A pattern variable is introduced by do S while (e) iff
			//   - (i) it is introduced by e when false and
			//   - (ii) S does not contain a reachable break statement
			//          for which the do statement is the break target.
			for (PatternScope scope : filteredChildScopes.stream().filter(scope -> scope instanceof PatternScope)
				.map(scope -> (PatternScope) scope).toList()) {
				if (!scope.matches && hasNoReachableBreakWithTarget(ctDo.getBody(), ctDo)) {
					result.add(scope.with(ctDo, false));
				}
			}

			return result;
		}

		if (parent instanceof CtFor ctFor) {
			List<Scope> result = new ArrayList<>();
			if (child != ctFor.getExpression()) {
				filteredChildScopes = exploreBranchForNewScopes(ctFor.getExpression());
			}

			if (ctFor.getForInit() != null) {
				for (CtStatement ctStatement : ctFor.getForInit()) {
					for (var scope : exploreBranchForNewScopes(ctStatement)) {
						// Any variable introduced by the initialization of a for statement is in scope for the condition, update and body of the for statement.
						result.add(new VariableScope(scope.ctVariable(), ctFor.getExpression()));
						for (var update : ctFor.getForUpdate()) {
							result.add(new VariableScope(scope.ctVariable(), update));
						}
						result.add(new VariableScope(scope.ctVariable(), ctFor.getBody()));
					}
				}
			}

			// The following rules apply to a basic for statement (§14.14.1):
			for (PatternScope scope : filteredChildScopes.stream().filter(scope -> scope instanceof PatternScope)
				.map(scope -> (PatternScope) scope).toList()) {
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
				if (!scope.matches && hasNoReachableBreakWithTarget(ctFor.getBody(), ctFor)) {
					result.add(scope.with(ctFor, false));
				}

				// FIXME: The JLS states for enhanced for statements:
				//
				// An enhanced for statement (§14.14.2) is defined by translation to a basic for statement, so no special rules need
				// to be provided for it.
				//
				// This translation is defined in https://docs.oracle.com/javase/specs/jls/se25/html/jls-14.html#jls-14.14.2
				//
				// If the type of Expression is a subtype of Iterable, then the basic for statement has this form:
				//
				// for (I #i = Expression.iterator(); #i.hasNext(); ) {
				//     {VariableModifier} T VarDeclId = (TargetType) #i.next();
				//     Statement
				// }
				//
				// (a similar translation is defined for arrays)
				//
				// The variable can only be introduced through the condition expression, which is the `#i.hasNext()`.
				// `#i` is an automatically generated identifier
				// -> it should be impossible to introduce a pattern variable through the enhanced for statement.
				//
				// The generated definition for the variable might introduce a pattern variable, but this does not seem possible
				// as of Java 25.
			}

			return result;
		}

		// For local variables introduced in expressions, special rules apply (these are only type patterns).
		if (parent instanceof CtExpression<?>) {
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
					.filter(scope -> scope instanceof PatternScope)
					.map(scope -> (PatternScope) scope)
					.filter(scope -> scope.matches() == (operator.getKind() == BinaryOperatorKind.AND))
					.map(scope -> scope.with(operator, scope.matches()))
					.toList();
			}

			if (parent instanceof CtUnaryOperator<?> operator && operator.getKind() == UnaryOperatorKind.NOT) {
				// For !(expr) the following holds:
				// - If a pattern variable is introduced by expr when true, then it is available for matching when false.
				// - If a pattern variable is introduced by expr when false, then it is available for matching when true.

				return filteredChildScopes.stream()
					.filter(scope -> scope instanceof PatternScope)
					.map(scope -> (PatternScope) scope)
					// swap the matches, because the operator is a NOT, matchesTrue will become matchesFalse and vice versa
					.map(scope -> scope.with(operator, !scope.matches))
					.toList();
			}

			if (parent instanceof CtBinaryOperator<?> operator && operator.getKind() == BinaryOperatorKind.INSTANCEOF) {
				List<Scope> result = new ArrayList<>();
				// This handles both regular type patterns and record patterns that could introduce pattern variables too
				for (var ctTypePattern : operator.getRightHandOperand().getElements(new TypeFilter<>(CtTypePattern.class))) {
					result.add(new PatternScope(ctTypePattern.getVariable(), operator, true));
				}

				return result;
			}
		}

		return List.of();
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
