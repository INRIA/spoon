/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAbstractSwitch;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCFlowBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCasePattern;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLabelledFlowBreak;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtPattern;
import spoon.reflect.code.CtRecordPattern;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.chain.CtQueryAware;
import spoon.reflect.visitor.chain.CtQueryable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		// Search input and then all parents until first CtPackage for element which may represents the declaration of this local variable
		while (scopeElement != null && !(scopeElement instanceof CtPackage) && scopeElement.isParentInitialized()) {
			CtElement parent = scopeElement.getParent();
			if (parent instanceof CtType<?>) {
				isTypeOnTheWay = true;
				// visit each CtField of `parent` CtType
				CtQuery q = parent.map(new AllTypeMembersFunction(CtField.class));
				q.forEach((CtField<?> field) -> {
					if (isInStaticScope && !field.hasModifier(ModifierKind.STATIC)) {
						/*
						 * the variable reference is used in static scope,
						 * but the field is not static - ignore it
						 */
						return;
					}
					// else send field as potential variable declaration
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

			if (parent instanceof CtModifiable ctModifiable) {
				isInStaticScope = isInStaticScope || ctModifiable.hasModifier(ModifierKind.STATIC);
			}
			scopeElement = parent;
		}
	}

	/**
	 * Interface for all scopes
	 */
	private interface Scope {
		/**
		 * The variable this scope applies to.
		 *
		 * @return the variable
		 */
		CtVariable<?> ctVariable();

		/**
		 * The element in which the variable can be referenced.
		 *
		 * @return the element
		 */
		CtElement ctElement();

		/**
		 * Checks whether the scope applies to the given element.
		 *
		 * @param ctElement the element to check.
		 * @return true if it applies, false if it does not
		 */
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
		public PatternScope with(CtElement ctElement, boolean matches) {
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
	private static List<Scope> exploreBranchForNewScopes(CtQueryable branch) {
		List<Scope> result = new ArrayList<>();

		// Any type pattern is okay, the sibling branches will be explored by the updateChildScopesForParent.
		CtElement child = branch.filterChildren(new TypeFilter<>(CtVariable.class)).first();
		while (child != null && child != branch) {
			// The children should always have a parent, given that they are children of the branch.
			CtElement parent = child.getParent();
			result = updateChildScopesForParent(result, child, parent);
			child = parent;
		}

		// If the branch itself is a variable declaration, then this variable is in scope for the branch.
		// The updateChildScopesForParent would have introduced it, but it was not called, because of the loop conidition
		// -> it has to be manually added here
		if (child == branch && child instanceof CtVariable<?> ctVariable) {
			result.add(new VariableScope(ctVariable, ctVariable));
		}

		return result.stream().filter(scope -> scope.ctElement() == branch).toList();
	}

	@SuppressWarnings("unchecked")
	private static <T> List<T> castList(List<?> sourceList) {
		return (List<T>) sourceList;
	}

	private static List<PatternScope> exploreBranchForNewPatternScopes(CtExpression<?> branch) {
		return castList(exploreBranchForNewScopes(branch));
	}

	private static List<PatternScope> exploreBranchForNewPatternScopes(CtPattern branch) {
		return castList(exploreBranchForNewScopes(branch));
	}

	private static boolean completesNormally(CtStatement statement) {
		// FIXME: The JLS has a definition for what "cannot complete normally" is
		return !(statement instanceof CtStatementList ctStatementList
			&& !ctStatementList.getStatements().isEmpty()
			&& ctStatementList.getLastStatement() instanceof CtCFlowBreak);
	}

	/**
	 * Checks if the statement contains a (potentially reachable) break with a label of the statement.
	 * <p>
	 * For example: {@code label: while(true) { break label; }} would be true.
	 *
	 * @param ctStatement the statement to check
	 * @return true if the statement has no label or no break where the target matches the label of the statement, false otherwise
	 */
	private static boolean hasNoReachableBreakWithTarget(CtStatement ctStatement) {
		// FIXME: This does not check whether the break statement is actually reachable
		return ctStatement.getLabel() == null || (ctStatement instanceof CtBodyHolder bodyHolder ? bodyHolder.getBody() : ctStatement)
			.filterChildren(new TypeFilter<>(CtBreak.class))
			.filterChildren((CtLabelledFlowBreak ctBreak) -> ctBreak.getLabelledStatement() == ctStatement)
			.first() == null;
	}

	/**
	 * Updates the child scopes for the parent.
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

		// This needs special handling, because of how the code is structured.
		//
		// The code related to type patterns operates on PatternScopes, and without this
		// if, the variables would be introduced as VariableScopes. The code that would
		// then update these scopes for type patterns wouldn't be called.
		if (child instanceof CtVariable<?> ctVariable && parent instanceof CtTypePattern) {
			return List.of(new PatternScope(ctVariable, parent, true));
		}

		if (child instanceof CtVariable<?> ctVariable) {
			filteredChildScopes.add(new VariableScope(ctVariable, ctVariable));
		}

		// Pattern scopes are introduced by a pattern and will change through parent expressions or statements, see
		// https://docs.oracle.com/javase/specs/jls/se25/html/jls-6.html#jls-6.3.1 and the following sections.
		//
		// The code might promote a pattern to a regular variable scope, in which case the other rules will apply to it.
		List<Scope> result = new ArrayList<>(updatePatternScopesForParent(filteredChildScopes.stream()
			.filter(scope -> scope instanceof PatternScope)
			.map(scope -> (PatternScope) scope)
			.toList(), child, parent));

		if (parent instanceof CtCase<?> ctCase) {
			// These apply to the entire case so they are discovered and returned by the exploreBranch.
			// This is used in the CtAbstractSwitch if to handle variables from other cases being accessible in their scopes
			for (var statement : ctCase.getStatements()) {
				if (statement instanceof CtLocalVariable<?> ctLocalVariable) {
					result.add(new VariableScope(ctLocalVariable, ctCase));
				}
			}

			// The following rules apply to a switch expression with a switch block consisting of switch labeled statement groups (§14.11.1):
			//
			// A pattern variable introduced by a statement S contained in a switch labeled statement group is definitely matched at all the
			// statements following S, if any, in the switch labeled statement group.
			if (ctCase.getStatements().stream().anyMatch(ctStatement -> child == ctStatement)) {
				for (var statement : child.map(new SiblingsFunction().mode(SiblingsFunction.Mode.PREVIOUS)).list(CtStatement.class)) {
					if (statement instanceof CtLocalVariable<?>) { // These have already been added to the result
						continue;
					}

					for (var scope : exploreBranchForNewScopes(statement)) {
						result.add(new VariableScope(scope.ctVariable(), child));
					}
				}
			}

			return result;
		}

		// A CtAbstractSwitch can be either a CtSwitch or a CtSwitchExpression,
		if (parent instanceof CtAbstractSwitch<?> && child instanceof CtCase<?> caseElement) {
			List<CtCase<?>> previousSiblings = caseElement.map(new SiblingsFunction().mode(SiblingsFunction.Mode.PREVIOUS))
				.select(new TypeFilter<>(CtCase.class))
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

		if (parent instanceof CtTryWithResource ctTryWith) {
			for (var resource : ctTryWith.getResources()) {
				for (var scope : resource == child ? filteredChildScopes : exploreBranchForNewScopes(resource)) {
					result.add(new VariableScope(scope.ctVariable(), ctTryWith.getBody()));
				}
			}

			return result;
		}

		// This covers (CtBodyHolder):
		// - CtCatch,
		// - CtExecutable<R>: CtAnnotationMethod<T>, CtAnonymousExecutable, CtConstructor<T>, CtLambda<T>, CtMethod<T>
		// - CtLoop: CtDo, CtFor, CtForEach, CtWhile
		// - CtTry: CtTryWithResource,

		// TODO: With the current implementation any new CtBodyHolders will be resolved by the below code, potentially causing
		//       wrong variable resolution. Instead one could constrain the below if to
		//       if (parent instanceof CtCatch || parent instanceof CtExecutable || ...)
		//       Then in any new implementation the variables would resolve to null instead of a wrong variable (trickier to discover)

		if (parent instanceof CtBodyHolder) {
			result.addAll(child
				.map(new SiblingsFunction().mode(SiblingsFunction.Mode.PREVIOUS))
				//select only CtVariable nodes
				.select(new TypeFilter<>(CtVariable.class))
				.list(CtVariable.class)
				.stream()
				.map(variable -> (Scope) new VariableScope(variable, child))
				.toList());
		}

		return result;
	}

	/**
	 * Updates the given scopes based on rules defined in
	 * <a href=https://docs.oracle.com/javase/specs/jls/se25/html/jls-6.html#jls-6.3.1>JLS-6.3.1</a> or
	 * <a href=https://docs.oracle.com/javase/specs/jls/se25/html/jls-6.html#jls-6.3.2>JLS-6.3.2</a>
	 *
	 * @param scopes the pattern scopes applying to the child
	 * @param child the child of the given parent ({@code child.getParent() == parent})
	 * @param parent the parent of the child
	 * @return a list of scopes that apply to the child, a sibling or the parent. If no rules apply, an empty list will be returned.
	 */
	private static List<Scope> updatePatternScopesForParent(Collection<PatternScope> scopes, CtElement child, CtElement parent) {
		if (parent instanceof CtExpression<?> ctExpression) {
			return new ArrayList<>(updateChildScopesForParentExpression(scopes, child, ctExpression));
		}

		if (parent instanceof CtBlock<?>) {
			// The following rule applies to a block statement S contained in a block (§14.2) that is not a switch block (§14.11.1):
			// - A pattern variable introduced by S is definitely matched at all the block statements following S, if any, in the block.

			// The child would be a statement -> visit all previous statements and check which ones apply:
			return child.map(new SiblingsFunction().mode(SiblingsFunction.Mode.PREVIOUS))
				.list(CtStatement.class)
				.stream()
				.flatMap(sibling -> exploreBranchForNewScopes(sibling).stream())
				.map(scope -> (Scope) new VariableScope(scope.ctVariable(), child))
				.toList();
		}

		// An if without a then statement `if (a);` can introduce pattern variables, but they can only be referenced from the
		// then, which does not exist -> these can be ignored.
		if (parent instanceof CtIf ctIf && ctIf.getThenStatement() != null) {
			List<Scope> result = new ArrayList<>();

			boolean canThenComplete = completesNormally(ctIf.getThenStatement());

			// The child could be:
			// - the condition (can introduce scopes)
			// - the then branch
			// - the else branch
			//
			// According to the JLS scopes can only be introduced by the condition
			// -> scopes from the then/else branch should be discarded
			for (PatternScope scope : ctIf.getCondition() == child ? scopes : exploreBranchForNewPatternScopes(ctIf.getCondition())) {
				// For an `if (e) S` (with maybe an else), a pattern variable introduced by `e` when `true` is definitely matched at `S`.
				if (scope.matches()) {
					result.add(scope.with(ctIf.getThenStatement(), true));
				}

				// A pattern variable is introduced by `if (e) S` iff
				// -  (i) it is introduced by e when false and
				// - (ii) S cannot complete normally.
				if (ctIf.getElseStatement() == null && !scope.matches() && !canThenComplete) {
					result.add(scope.with(ctIf, false));
				}

				// The following rules apply to a statement `if (e) S else T`:
				if (ctIf.getElseStatement() != null) {
					// A pattern variable introduced by e when false is definitely matched at T.
					if (!scope.matches()) {
						result.add(scope.with(ctIf.getElseStatement(), false));
					}

					// A pattern variable is introduced by if (e) S else T iff either:
					// - It is introduced by e when true, and S can complete normally, and T cannot complete normally; or
					boolean canElseComplete = completesNormally(ctIf.getElseStatement());
					if (scope.matches() && canThenComplete && !canElseComplete) {
						result.add(scope.with(ctIf, true));
					}

					// - It is introduced by e when false, and S cannot complete normally, and T can complete normally.
					if (!scope.matches() && !canThenComplete && canElseComplete) {
						result.add(scope.with(ctIf, false));
					}
				}
			}

			return result;
		}

		if (parent instanceof CtWhile ctWhile) {
			List<Scope> result = new ArrayList<>();

			for (PatternScope scope : child == ctWhile.getLoopingExpression() ? scopes :  exploreBranchForNewPatternScopes(ctWhile.getLoopingExpression())) {
				// The following rules apply to a statement `while (e) S`:

				// A pattern variable introduced by e when true is definitely matched at S.
				if (scope.matches()) {
					result.add(scope.with(ctWhile.getBody(), true));
				}

				// A pattern variable is introduced by while (e) S iff
				// - (i) it is introduced by e when false and
				// - (ii) S does not contain a reachable break statement for which the while
				//        statement is the break target
				if (!scope.matches() && hasNoReachableBreakWithTarget(ctWhile)) {
					result.add(scope.with(ctWhile, false));
				}
			}

			return result;
		}

		if (parent instanceof CtDo ctDo) {
			List<Scope> result = new ArrayList<>();
			// The following rule applies to a statement `do S while (e)`:
			// - A pattern variable is introduced by do S while (e) iff
			//   - (i) it is introduced by e when false and
			//   - (ii) S does not contain a reachable break statement
			//          for which the do statement is the break target.
			for (PatternScope scope : child == ctDo.getLoopingExpression() ? scopes : exploreBranchForNewPatternScopes(ctDo.getLoopingExpression())) {
				if (!scope.matches() && hasNoReachableBreakWithTarget(ctDo)) {
					result.add(scope.with(ctDo, false));
				}
			}

			return result;
		}

		if (parent instanceof CtFor ctFor) {
			List<Scope> result = new ArrayList<>();

			// The following rules apply to a basic for statement (§14.14.1):
			for (PatternScope scope : ctFor.getExpression() == child ? scopes : exploreBranchForNewPatternScopes(ctFor.getExpression())) {
				// - A pattern variable introduced by the condition expression when true is definitely
				//   matched at both the incrementation part and the contained statement.
				if (scope.matches()) {
					for (var update : ctFor.getForUpdate()) {
						result.add(scope.with(update, true));
					}

					result.add(scope.with(ctFor.getBody(), true));
				}

				// - A pattern variable is introduced by a basic for statement iff
				//   - (i) it is introduced by the condition expression when false and
				//   - (ii) the contained statement, S, does not contain a reachable break for which the
				//          basic for statement is the break target.
				if (!scope.matches() && hasNoReachableBreakWithTarget(ctFor)) {
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

		// The following rule applies to a labeled statement (§14.7):
		// - A pattern variable is introduced by a labeled statement L: S (where L is a label) iff
		//    (i) it is introduced by the statement S, and
		//   (ii) S does not contain a reachable break statement for which the labeled statement is the break target (§14.15).
		//
		// The code where this is relevant (loops) already handles this condition.
		//
		// If necessary, one could implement this rule at the call site of this function like this:
		// if (parent instanceof CtStatement ctStatement && !hasNoReachableBreakWithTarget(ctStatement)) {
		//     // filter out all scopes that apply to the full ctStatement
		// }

		// the other parts defined for switch statements are handled in the updateChildScopesForParent
		if (parent instanceof CtCase<?> ctCase) {
			// The following rule applies to a switch expression with a switch block consisting of switch rules:
			//
			// A pattern variable introduced by a switch label is definitely matched in the associated switch rule
			// expression, switch rule block, or switch rule throw statement.
			//
			// -> All variables defined in the case expressions will be in scope for the case
			List<Scope> result = new ArrayList<>();
			for (var expr : ctCase.getCaseExpressions()) {
				for (var scope : expr == child ? scopes : exploreBranchForNewPatternScopes(expr)) {
					// NOTE: This assumes negated patterns can not appear in the case expression
					result.add(new VariableScope(scope.ctVariable(), ctCase));
				}
			}

			return result;
		}

		return List.of();
	}

	/**
	 * Updates the scopes for the given parent expression based on the rules defined in
	 * <a href=https://docs.oracle.com/javase/specs/jls/se25/html/jls-6.html#jls-6.3.1>JLS-6.3.1</a>
	 *
	 * @param filteredChildScopes the scopes that apply to the child (the ones that do not apply should already be filtered out)
	 * @param child the child of the given parent ({@code child.getParent() == parent})
	 * @param parent the parent of the child
	 * @return a list of scopes that apply to the child, a sibling or the parent. If no rules apply, an empty list will be returned.
	 */
	private static List<PatternScope> updateChildScopesForParentExpression(Collection<PatternScope> filteredChildScopes, CtElement child, CtExpression<?> parent) {
		// A variable defined in a child of a record pattern applies to the record as well, so these are passed along.
		if (parent instanceof CtRecordPattern ctRecordPattern) {
			List<PatternScope> result = new ArrayList<>();

			for (var ctPattern : ctRecordPattern.getPatternList()) {
				for (var scope : ctPattern == child ? filteredChildScopes : exploreBranchForNewPatternScopes(ctPattern)) {
					result.add(scope.with(ctRecordPattern, scope.matches()));
				}
			}

			return result;
		}

		// This represents a `case <Pattern>` where the pattern could for example be a `case String string`.
		if (parent instanceof CtCasePattern ctCasePattern) {
			// The scope applies to the parent as well
			return filteredChildScopes.stream().map(scope -> scope.with(ctCasePattern, scope.matches())).toList();
		}

		// Besides that there is unnamed pattern, but it does not introduce any variables -> it can be ignored here

		if (parent instanceof CtBinaryOperator<?> operator && (operator.getKind() == BinaryOperatorKind.AND
			|| operator.getKind() == BinaryOperatorKind.OR)) {
			// In `a <op> b`, both a and b can introduce pattern variables, but the passed child scopes will be for only one of the
			// branches. This will add the other branch's scopes as well:
			CtExpression<?> otherBranch =
				operator.getLeftHandOperand() == child ? operator.getRightHandOperand() : operator.getLeftHandOperand();

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
			return Stream.concat(filteredChildScopes.stream(), exploreBranchForNewPatternScopes(otherBranch).stream())
				.filter(scope -> scope.matches() == (operator.getKind() == BinaryOperatorKind.AND))
				.map(scope -> scope.with(operator, scope.matches()))
				.toList();
		}

		if (parent instanceof CtUnaryOperator<?> operator && operator.getKind() == UnaryOperatorKind.NOT) {
			// For !(expr) the following holds:
			// - If a pattern variable is introduced by expr when true, then it is available for matching when false.
			// - If a pattern variable is introduced by expr when false, then it is available for matching when true.

			return filteredChildScopes.stream()
				// swap the matches, because the operator is a NOT, matchesTrue will become matchesFalse and vice versa
				.map(scope -> scope.with(operator, !scope.matches()))
				.toList();
		}

		if (parent instanceof CtConditional<?> ctConditional) {
			return (ctConditional.getCondition() == child ? filteredChildScopes : exploreBranchForNewPatternScopes(ctConditional.getCondition()))
				.stream()
				// The following rules apply to a conditional expression a ? b : c (§15.25):
				// - A pattern variable introduced by a when true is definitely matched at b.
				// - A pattern variable introduced by a when false is definitely matched at c.
				.map(scope -> scope.with(scope.matches() ? ctConditional.getThenExpression() : ctConditional.getElseExpression(), scope.matches()))
				.toList();
		}

		if (parent instanceof CtBinaryOperator<?> operator && operator.getKind() == BinaryOperatorKind.INSTANCEOF) {
			// The following rule applies to an instanceof expression with a pattern operand, a instanceof p (§15.20.2):
			// - A pattern variable is introduced by a instanceof p when true iff the pattern p contains a
			//   declaration of the pattern variable (§14.30.1).

			// The child might be the left hand, but patterns only come from the right hand side
			// -> this branch must be explored here to not miss patterns
			return (operator.getLeftHandOperand() == child ? exploreBranchForNewPatternScopes(operator.getRightHandOperand()).stream() : filteredChildScopes.stream())
				.map(scope -> scope.with(operator, scope.matches()))
				.toList();
		}

		// CtSwitchExpression is handled in updateChildScopesForParent, because the described behavior is very similar
		// to the old switch style.

		// Parenthesized Expressions are not a distinct type in spoon, so they do not need special handling.

		return List.of();
	}

	/**
	 * @param var a declaration the user of this class might be looking for
	 * @param output the output to which to pass the declaration if it matches the searched variable name (if any)
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
