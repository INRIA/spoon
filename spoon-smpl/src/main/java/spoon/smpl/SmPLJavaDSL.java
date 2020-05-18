package spoon.smpl;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;

import java.util.ArrayList;
import java.util.List;

// TODO: add names for metavariable types (identifier, type, constant ...)

/**
 * Utilities to define and facilitate working with the SmPL Java DSL.
 */
public class SmPLJavaDSL {
    /**
     * Name of the field used to hold the name of the rule.
     */
    private static final String ruleNameFieldName = "__SmPLRuleName__";

    /**
     * Get the name of the field used to hold the name of the rule.
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
     * @return Name of the method used to encode metavariable definitions
     */
    public static String getMetavarsMethodName() {
        return metavarsMethodName;
    }

    /**
     * Name of executable used to encode dots statements.
     */
    private static final String dotsElementName = "__SmPLDots__";

    /**
     * Get name of executable used to encode dots statements.
     * @return Name of executable used to encode dots statements
     */
    public static String getDotsElementName() {
        return dotsElementName;
    }

    /**
     * Name of executable used to encode "when != x" constraints on dots.
     */
    private static final String dotsWhenNotEqualName = "whenNotEqual";

    /**
     * Get name of executable used to encode "when != x" constraints on dots.
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
     * @return Name of executable used to encode deleted lines available for anchoring
     */
    public static String getDeletionAnchorName() {
        return deletionAnchorName;
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
     * Check if a given element represents a deletion anchor in the SmPL Java DSL.
     * @param e Element to check
     * @return True if element represents a deletion anchor, false otherwise
     */
    public static boolean isDeletionAnchor(CtElement e) {
        return isExecutableWithName(e, deletionAnchorName);
    }

    /**
     * Given a CtClass in the SmPL Java DSL, find the method encoding the matching/transformation
     * rule.
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
        return isExecutableWithName(e, dotsElementName);
    }

    /**
     * Check if a given element represents a parameter (or argument) -level SmPL dots operator.
     *
     * @param e Element to check
     * @return True if element represents a parameter (or argument) -level SmPL dots operator, false otherwise
     */
    public static boolean isParameterLevelDots(CtElement e) {
        return (e instanceof CtParameter && ((CtParameter<?>) e).getSimpleName().equals(dotsElementName))
               || (e instanceof CtVariableRead && ((CtVariableRead<?>) e).getVariable().getSimpleName().equals(dotsElementName));
    }

    /**
     * Create a source code String for a parameter (or argument) -level SmPL dots operator.
     *
     * @return Source code string for a parameter (or argument) -level SmPL dots operator
     */
    public static String createDotsParameterString() {
        // TODO: should these be unique / fresh identifiers?
        return "Object " + dotsElementName;
    }

    /**
     * Given a CtInvocation representing an SmPL dots construct in the SmPL Java DSL, collect
     * all arguments provided in "when != x" constraints.
     *
     * @param dots Element representing an SmPL dots construct
     * @return List of arguments x provided in "when != x" constraints
     */
    public static List<String> getWhenNotEquals(CtInvocation<?> dots) {
        List<String> result = new ArrayList<>();

        for (CtExpression<?> stmt : dots.getArguments()) {
            if (isWhenNotEquals(stmt)) {
                CtVariableRead<?> read = (CtVariableRead<?>) ((CtInvocation<?>) stmt).getArguments().get(0);
                result.add(read.getVariable().getSimpleName());
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
     * Check if a given AST element is an invocation of a given executable name.
     *
     * @param e Element to check
     * @param name Executable name to match
     * @return True if the given element is an invocation matching the given executable name, false otherwise
     */
    private static boolean isExecutableWithName(CtElement e, String name) {
        return e instanceof CtInvocation<?>
               && ((CtInvocation<?>) e).getExecutable().getSimpleName().equals(name);
    }
}
