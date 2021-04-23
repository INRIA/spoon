/*
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package spoon.smpl;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;

import java.util.ArrayList;
import java.util.List;

// TODO: add names for metavariable types (identifier, type, constant ...)
// TODO: grouping of public and private members

/**
 * Utilities to define and facilitate working with the SmPL Java DSL.
 */
public class SmPLJavaDSL {
	/**
	 * Hide utility class constructor.
	 */
	private SmPLJavaDSL() { }

	/**
	 * Name of the field used to hold the name of the rule.
	 */
	private static final String ruleNameFieldName = "__SmPLRuleName__";

	/**
	 * Get the name of the field used to hold the name of the rule.
	 *
	 * @return Name of the field used to hold the name of the rule
	 */
	public static String getRuleNameFieldName() {
		return ruleNameFieldName;
	}

	/**
	 * Name of the method used to encode metavariable definitions.
	 */
	private static final String metavarsMethodName = "__SmPLMetavars__";

	/**
	 * Get name of the method used to encode metavariable definitions.
	 *
	 * @return Name of the method used to encode metavariable definitions
	 */
	public static String getMetavarsMethodName() {
		return metavarsMethodName;
	}

	/**
	 * Name of executable used to encode dots statements.
	 */
	private static final String dotsStatementElementName = "__SmPLDots__";

	/**
	 * Get name of executable used to encode dots statements.
	 *
	 * @return Name of executable used to encode dots statements
	 */
	public static String getDotsStatementElementName() {
		return dotsStatementElementName;
	}

	/**
	 * Name of executable used to encode dots parameters and call arguments.
	 */
	private static final String dotsParameterOrArgumentElementName = "__SmPLDotsArg__";

	/**
	 * Get name of executable used to encode dots parameters and call arguments.
	 *
	 * @return Name of executable used to encode dots parameters and call arguments.
	 */
	public static String getDotsParameterOrArgumentElementName() {
		return dotsParameterOrArgumentElementName;
	}

	/**
	 * Name of executable used to encode "when != x" constraints on dots.
	 */
	private static final String dotsWhenNotEqualName = "whenNotEqual";

	/**
	 * Get name of executable used to encode "when != x" constraints on dots.
	 *
	 * @return Name of executable used to encode "when != x" constraints on dots
	 */
	public static String getDotsWhenNotEqualName() {
		return dotsWhenNotEqualName;
	}

	/**
	 * Name of executable used to encode "when exists" constraints on dots.
	 */
	private static final String dotsWhenExistsName = "whenExists";

	/**
	 * Get name of executable used to encode "when exists" constraints on dots.
	 *
	 * @return Name of executable used to encode "when exists" constraints on dots
	 */
	public static String getDotsWhenExistsName() {
		return dotsWhenExistsName;
	}

	/**
	 * Name of executable used to encode "when any" constraints on dots.
	 */
	private static final String dotsWhenAnyName = "whenAny";

	/**
	 * Get name of executable used to encode "when any" constraints on dots.
	 *
	 * @return Name of executable used to encode "when any" constraints on dots
	 */
	public static String getDotsWhenAnyName() {
		return dotsWhenAnyName;
	}

	/**
	 * Name of executable used to encode deleted lines available for anchoring.
	 */
	private static final String deletionAnchorName = "__SmPLDeletion__";

	/**
	 * Get name of executable used to encode deleted lines available for anchoring.
	 *
	 * @return Name of executable used to encode deleted lines available for anchoring
	 */
	public static String getDeletionAnchorName() {
		return deletionAnchorName;
	}

	/**
	 * Check if a given element represents a deletion anchor in the SmPL Java DSL.
	 *
	 * @param e Element to check
	 * @return True if element represents a deletion anchor, false otherwise
	 */
	public static boolean isDeletionAnchor(CtElement e) {
		return isExecutableWithName(e, deletionAnchorName);
	}

	/**
	 * Name for elements that indicate missing/unspecified information.
	 */
	private static final String unspecifiedElementOrTypeName = "__SmPLUnspecified__";

	/**
	 * Get name for elements that indicate missing/unspecified information.
	 *
	 * @return Name for elements that indicate missing/unspecified information
	 */
	public static String getUnspecifiedElementOrTypeName() {
		return unspecifiedElementOrTypeName;
	}

	/**
	 * Name for elements that indicate unsupported elements that have been substituted.
	 */
	private static String unsupportedElementName = "__SmPLUnsupported__";

	/**
	 * Get name for elements that indicate unsupported elements that have been substituted.
	 *
	 * @return Name for elements that indicate unsupported elements that have been substituted
	 */
	public static String getUnsupportedElementName() {
		return unsupportedElementName;
	}

	/**
	 * Name for element indicating a dots-with-optional-match SmPL construct.
	 */
	private static String dotsWithOptionalMatchName = "__SmPLDotsOptionalMatch__";

	/**
	 * Get name for element indicating a dots-with-optional-match SmPL construct.
	 *
	 * @return Name for element indicating a dots-with-optional-match SmPL construct
	 */
	public static String getDotsWithOptionalMatchName() {
		return dotsWithOptionalMatchName;
	}

	/**
	 * Name for element indicating the beginning of an SmPL pattern disjunction.
	 */
	private static String beginDisjunctionName = "__SmPLBeginDisjunction__";

	/**
	 * Get name for element indicating the beginning of an SmPL pattern disjunction.
	 *
	 * @return Name for element indicating the beginning of an SmPL pattern disjunction
	 */
	public static String getBeginDisjunctionName() {
		return beginDisjunctionName;
	}

	/**
	 * Name for element indicating a continuation (clause-separator) of an SmPL pattern disjunction.
	 */
	private static String continueDisjunctionName = "__SmPLContinueDisjunction__";

	/**
	 * Get name for element indicating a continuation (clause-separator) of an SmPL pattern disjunction.
	 *
	 * @return Name for element indicating a continuation (clause-separator) of an SmPL pattern disjunction
	 */
	public static String getContinueDisjunctionName() {
		return continueDisjunctionName;
	}

	/**
	 * Check if a given element represents the beginning of an SmPL pattern disjunction.
	 *
	 * @param element Element to check
	 * @return True if element represents the beginning of an SmPL pattern disjunction, false otherwise
	 */
	public static boolean isBeginDisjunction(CtElement element) {
		return isIfStatementWithNamedConditionVariable(element, beginDisjunctionName);
	}

	/**
	 * Check if a given element represents a continuation (clause-separator) of an SmPL pattern disjunction.
	 *
	 * @param element Element to check
	 * @return True if element represents a continuation (clause-separator) of an SmPL pattern disjunction, false otherwise
	 */
	public static boolean isContinueDisjunction(CtElement element) {
		return isIfStatementWithNamedConditionVariable(element, continueDisjunctionName);
	}

	/**
	 * Check if a given element represents a dots-with-optional-match SmPL construct.
	 *
	 * @param element Element to check
	 * @return True if element represents a dots-with-optional-match SmPL construct, false otherwise
	 */
	public static boolean isDotsWithOptionalMatch(CtElement element) {
		return (element instanceof CtIf
				&& isExecutableWithName(((CtIf) element).getCondition(), dotsWithOptionalMatchName))
				|| isExecutableWithName(element, dotsWithOptionalMatchName); // TODO: maybe separate these cases
	}

	/**
	 * Name for a wrapper element marking its descendant as a match on an expression rather than a full statement.
	 */
	private static String expressionMatchWrapperName = "__SmPLExpressionMatch__";

	/**
	 * Get name for a wrapper element marking its descendant as a match on an expression rather than a full statement.
	 *
	 * @return Name for a wrapper element marking its descendant as a match on an expression rather than a full statement
	 */
	public static String getExpressionMatchWrapperName() {
		return expressionMatchWrapperName;
	}

	/**
	 * Check if a given element is a wrapper element marking its descendant as a match on an expression rather than a
	 * full statement.
	 *
	 * @param element Element to check
	 * @return True if element is a wrapper element marking its descendant as a match on an expression, false otherwise
	 */
	public static boolean isExpressionMatchWrapper(CtElement element) {
		return isExecutableWithName(element, expressionMatchWrapperName);
	}

	/**
	 * Get the wrapped descendant of a wrapper element.
	 *
	 * @param wrapper Wrapper element
	 * @return Wrapped descendant element
	 */
	public static CtElement getWrappedElement(CtElement wrapper) {
		if (wrapper instanceof CtInvocation) {
			return ((CtInvocation<?>) wrapper).getArguments().get(0);
		} else {
			throw new IllegalArgumentException("invalid wrapper element");
		}
	}

	/**
	 * Given a CtClass in the SmPL Java DSL, find the method encoding the matching/transformation
	 * rule.
	 *
	 * @param ctClass Class in SmPL Java DSL
	 * @return Method encoding the matching/transformation rule
	 */
	public static CtMethod<?> getRuleMethod(CtClass<?> ctClass) {
		for (CtMethod<?> method : ctClass.getMethods()) {
			if (!method.getSimpleName().equals(metavarsMethodName)) {
				return method;
			}
		}

		return null;
	}

	/**
	 * Check if a given element represents a statement-level SmPL dots operator.
	 *
	 * @param e Element to check
	 * @return True if element represents a statement-level SmPL dots operator, false otherwise
	 */
	public static boolean isStatementLevelDots(CtElement e) {
		return isExecutableWithName(e, dotsStatementElementName);
	}

	/**
	 * Check if a given element represents a parameter (or argument) -level SmPL dots operator.
	 *
	 * @param e Element to check
	 * @return True if element represents a parameter (or argument) -level SmPL dots operator, false otherwise
	 */
	public static boolean isParameterLevelDots(CtElement e) {
		return (e instanceof CtParameter && ((CtParameter<?>) e).getSimpleName().equals(dotsStatementElementName))
				|| (e instanceof CtVariableRead && ((CtVariableRead<?>) e).getVariable().getSimpleName().equals(dotsStatementElementName));
	}

	/**
	 * Create a source code String for a parameter (or argument) -level SmPL dots operator.
	 *
	 * @return Source code string for a parameter (or argument) -level SmPL dots operator
	 */
	public static String createDotsParameterString() {
		// TODO: should these be unique / fresh identifiers?
		return "Object " + dotsStatementElementName;
	}

	/**
	 * Given a CtInvocation representing an SmPL dots construct in the SmPL Java DSL, collect
	 * all arguments provided in "when != x" constraints.
	 *
	 * @param dots Element representing an SmPL dots construct
	 * @return List of arguments x provided in "when != x" constraints
	 */
	public static List<CtElement> getWhenNotEquals(CtInvocation<?> dots) {
		List<CtElement> result = new ArrayList<>();

		for (CtExpression<?> stmt : dots.getArguments()) {
			if (isWhenNotEquals(stmt)) {
				result.add(((CtInvocation<?>) stmt).getArguments().get(0));
			}
		}

		return result;
	}

	/**
	 * Check if a given element represents a "when != x" constraint on dots in the SmPL
	 * Java DSL.
	 *
	 * @param e Element to check
	 * @return True if element represents a "when != x" constraint, false otherwise
	 */
	public static boolean isWhenNotEquals(CtElement e) {
		return isExecutableWithName(e, dotsWhenNotEqualName);
	}

	/**
	 * Given a CtInvocation representing an SmPL dots construct in the SmPL Java DSL, check
	 * if the dots constructs specifies the "when exists" constraint relaxation.
	 *
	 * @param dots Element representing an SmPL dots construct
	 * @return True if dots construct specifies "when exists" relaxation, false otherwise
	 */
	public static boolean hasWhenExists(CtInvocation<?> dots) {
		return dots.getArguments().stream().anyMatch(SmPLJavaDSL::isWhenExists);
	}

	/**
	 * Check if a given element represents a "when exists" constraint relaxation on dots in the SmPL
	 * Java DSL.
	 *
	 * @param e Element to check
	 * @return True if element represents a "when exists" constraint relaxation, false otherwise
	 */
	public static boolean isWhenExists(CtElement e) {
		return isExecutableWithName(e, dotsWhenExistsName);
	}

	/**
	 * Given a CtInvocation representing an SmPL dots construct in the SmPL Java DSL, check
	 * if the dots constructs specifies the "when any" constraint relaxation.
	 *
	 * @param dots Element representing an SmPL dots construct
	 * @return True if dots construct specifies "when any" relaxation, false otherwise
	 */
	public static boolean hasWhenAny(CtInvocation<?> dots) {
		return dots.getArguments().stream().anyMatch(SmPLJavaDSL::isWhenAny);
	}

	/**
	 * Check if a given element represents a "when any" constraint relaxation on dots in the SmPL
	 * Java DSL.
	 *
	 * @param e Element to check
	 * @return True if element represents a "when any" constraint relaxation, false otherwise
	 */
	public static boolean isWhenAny(CtElement e) {
		return isExecutableWithName(e, dotsWhenAnyName);
	}

	/**
	 * Create a source code String for a representation of an unspecified method header (unspecified return type,
	 * name and list of parameters).
	 *
	 * @return Source code String for a method header that represents an unspecified method
	 */
	public static String createUnspecifiedMethodHeaderString() {
		return "void " + unspecifiedElementOrTypeName + "()";
	}

	/**
	 * Create a source code String for the method call expression part of the implicit dots construct that is added to
	 * a patch that does not match on the method header.
	 *
	 * @return Source code String for method call expression of the implicit dots construct
	 */
	public static String createImplicitDotsCall() {
		return dotsWithOptionalMatchName + "(" + dotsWhenExistsName + "())";
	}

	/**
	 * Check whether the given element is a CtMethod with a method header that is a representation of an
	 * unspecified method header (unspecified return type, name and list of parameters).
	 *
	 * @param e Element to check
	 * @return True if element represents an unspecified method header, false otherwise
	 */
	public static boolean isUnspecifiedMethodHeader(CtElement e) {
		return e instanceof CtMethod && ((CtMethod<?>) e).getSimpleName().equals(unspecifiedElementOrTypeName);
	}

	/**
	 * Check whether the given element is any of the DSL meta elements.
	 *
	 * @param e Element to check
	 * @return True if the element is any of the DSL meta elements, false otherwise
	 */
	public static boolean isMetaElement(CtElement e) {
		return isExpressionMatchWrapper(e) || isBeginDisjunction(e) || isContinueDisjunction(e)
				|| isStatementLevelDots(e) || isDotsWithOptionalMatch(e) || isDeletionAnchor(e)
				|| isParameterLevelDots(e) || isParameterLevelDots(e) || isWhenAny(e)
				|| isWhenExists(e) || isWhenNotEquals(e);
	}

	/**
	 * Check if a given AST element is an invocation of a given executable name.
	 *
	 * @param e    Element to check
	 * @param name Executable name to match
	 * @return True if the given element is an invocation matching the given executable name, false otherwise
	 */
	private static boolean isExecutableWithName(CtElement e, String name) {
		return e instanceof CtInvocation<?>
				&& ((CtInvocation<?>) e).getExecutable().getSimpleName().equals(name);
	}

	/**
	 * Check if a given AST element is an If statement with condition expression a single VariableRead of a variable
	 * of a certain name.
	 *
	 * @param element Element to check
	 * @param name    Variable name to match
	 * @return True if the given element is an If statement with condition variable of given name, false otherwise
	 */
	private static boolean isIfStatementWithNamedConditionVariable(CtElement element, String name) {
		return element instanceof CtIf
				&& ((CtIf) element).getCondition() instanceof CtVariableRead
				&& ((CtVariableRead<?>) ((CtIf) element).getCondition()).getVariable().getSimpleName().equals(name);
	}
}
