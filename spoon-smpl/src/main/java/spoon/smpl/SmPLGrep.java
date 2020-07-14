package spoon.smpl;

import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtScanner;

import java.util.ArrayList;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * SmPLGrep provides a facility for extracting a pattern of strings from an SmPL rule along with the corresponding
 * facility for matching such a pattern against the plain text source code of a given executable AST block. The
 * intention is to use SmPLGrep as a preprocessing step to reduce the number of target methods that need to be fully
 * model-checked, by excluding methods that cannot possibly match the rule simply based on the code not containing
 * certain strings contained in the rule.
 */
public class SmPLGrep {
    /**
     * Build a pattern from a given SmPL rule instance
     *
     * @param rule SmPL rule
     * @return Pattern representing strings required to be present in matching code
     */
    public static Pattern buildPattern(SmPLRule rule) {
        // Extract the source rule body (patch source excluding metavariable declarations), lowercase it and remove
        //   any addition lines
        String ruleBodySource = rule.getSource()
                                    .substring(rule.getSource().lastIndexOf("@@") + 2)
                                    .toLowerCase()
                                    .replaceAll("(?m)^\\+.+$", "");

        Pattern result = new Pattern();

        // A scanner for finding interesting strings
        CtScanner scanner = new CtScanner() {
            @Override
            public <T> void visitCtInvocation(CtInvocation<T> invocation) {
                if (SmPLJavaDSL.isMetaElement(invocation) && !SmPLJavaDSL.isExpressionMatchWrapper(invocation)) {
                    // Prevent scanning into literals and other stuff that is only part of the meta syntax
                    return;
                }

                super.visitCtInvocation(invocation);
            }

            @Override
            public void visitCtIf(CtIf ifElement) {
                if (SmPLJavaDSL.isDotsWithOptionalMatch(ifElement)) {
                    // Since optdots denote completely optional matches the only optdots we will scan are the implicit
                    //   dots added to a rule that doesnt match on the method header. Implicit dots will always be the
                    //   first statement in the rule body.
                    if (ifElement != ifElement.getParent(CtExecutable.class).getBody().getStatement(0)) {
                        return;
                    }
                }

                if (SmPLJavaDSL.isBeginDisjunction(ifElement)) {
                    result.enterDisjunction();
                } else if (SmPLJavaDSL.isContinueDisjunction(ifElement)) {
                    result.continueDisjunction();
                }

                super.visitCtIf(ifElement);
            }

            @Override
            protected void enter(CtElement e) {
                super.enter(e);

                if (SmPLJavaDSL.isMetaElement(e)) {
                    return;
                }

                // Dig out interesting strings. Only strings literally present in the patch source will be included in
                //   the final result.

                if (e instanceof CtIf) {
                    result.addString("if");
                }

                if (e instanceof CtNamedElement) {
                    check(((CtNamedElement) e).getSimpleName());
                }

                if (e instanceof CtTypeAccess && ((CtTypeAccess<?>) e).getAccessedType() != null) {
                    check(((CtTypeAccess<?>) e).getAccessedType().getSimpleName());
                }

                if (e instanceof CtTypeReference) {
                    check(((CtTypeReference<?>) e).getSimpleName());
                }

                if (e instanceof CtExecutableReference<?>) {
                    check(((CtExecutableReference<?>) e).getSimpleName());
                }

                if (e instanceof CtLiteral) {
                    check(((CtLiteral<?>) e).toString());
                }

                if (e instanceof CtVariableReference) {
                    check(((CtVariableReference<?>) e).getSimpleName());
                }

                if (e instanceof CtConstructorCall) {
                    result.addString("new");
                    check(((CtConstructorCall<?>) e).getType().getSimpleName());
                }

                if (e instanceof CtReturn) {
                    result.addString("return");
                }
            }

            protected void exit(CtElement e) {
                if (SmPLJavaDSL.isBeginDisjunction(e)) {
                    result.exitDisjunction();
                }
            }

            private void check(String s) {
                // Check if we should add a string (and if so, add it). Only strings literally present in the patch
                //   that are also not metavariable identifiers will be added.
                if (ruleBodySource.contains(s.toLowerCase())
                    && !rule.getMetavariableConstraints().containsKey(s)
                    && !rule.getMetavariableConstraints().containsKey(s.toLowerCase())) {
                    result.addString(s);
                }
            }
        };

        scanner.scan(rule.getMatchTargetDSL());
//        System.out.println(result);
        return result;
    }

    /**
     * Check if a given arbitrary executable AST block contains all the strings required by the given pattern.
     *
     * @param ctExecutable Executable to check
     * @param pattern Pattern representing required strings
     * @return True if all required strings are present, false otherwise
     */
    public static boolean isPatternMatch(CtExecutable<?> ctExecutable, Pattern pattern) {
        return pattern.matches(ctExecutable.toString());
    }

    /**
     * SmPLGrep.Pattern represents a set of Strings (and/or disjunctions over Strings) that all must be present in a
     * given target String for the target String to be considered as matching.
     *
     * For example, the pattern (foo & bar & (x | (y & z))) would match a String containing both substrings "foo" and
     * "bar", and either the substring "x" or both substrings "y" and "z".
     */
    public static class Pattern {
        /**
         * Base pattern element type.
         */
        private static abstract class PatternNode extends ArrayList<PatternNode> {
            public abstract boolean matches(String s);
        }

        /**
         * Pattern element for a single String.
         */
        private static class _String extends PatternNode {
            /**
             * Create a new single String pattern node.
             *
             * @param value String value
             */
            public _String(String value) {
                this.value = value;
                this.lowerCaseValue = value.toLowerCase();
            }

            /**
             * String value.
             */
            public final String value;

            /**
             * Lowercase String value;
             */
            public final String lowerCaseValue;

            /**
             * Check if a given target String contains this String.
             *
             * @param s Target String
             * @return True if target String contains this String, false otherwise
             */
            @Override
            public boolean matches(String s) {
                return s.contains(lowerCaseValue);
            }

            @Override
            public String toString() {
                return value;
            }

            @Override
            public boolean equals(Object other) {
                return other == this || (other instanceof _String && ((_String) other).lowerCaseValue.equals(lowerCaseValue));
            }

            /**
             * Disabled.
             *
             * @param patternNode Irrelevant
             * @return false
             */
            @Override
            public boolean add(PatternNode patternNode) {
                return false;
            }
        }

        /**
         * Pattern element representing a conjunction of other pattern elements.
         */
        private static class Conjunction extends PatternNode {
            /**
             * Check if a given target String matches all the elements of this conjunction.
             *
             * @param s Target String
             * @return True if target String matches all elements of the conjunction, false otherwise
             */
            @Override
            public boolean matches(String s) {
                return size() == 0 || stream().allMatch(node -> node.matches(s));
            }

            @Override
            public String toString() {
                if (size() == 1) {
                    return get(0).toString();
                } else {
                    return "(" + stream().map(PatternNode::toString).collect(Collectors.joining(" & ")) + ")";
                }
            }

            @Override
            public boolean add(PatternNode node) {
                return (node instanceof _String && contains(node)) ? false : super.add(node);
            }
        }

        /**
         * Pattern element representing a disjunction of other pattern elements.
         */
        private static class Disjunction extends PatternNode {
            /**
             * Check if a given target String contains some element of this disjunction.
             *
             * @param s Target String
             * @return True if target String contains any element of this disjunction, false otherwise
             */
            @Override
            public boolean matches(String s) {
                return size() == 0 || stream().anyMatch(node -> node.matches(s));
            }

            @Override
            public String toString() {
                if (size() == 1) {
                    return get(0).toString();
                } else {
                    return "(" + stream().map(PatternNode::toString).collect(Collectors.joining(" | ")) + ")";
                }
            }

            @Override
            public boolean add(PatternNode node) {
                return (node instanceof _String && contains(node)) ? false : super.add(node);
            }
        }

        /**
         * Create a new Pattern.
         */
        public Pattern() {
            patternNodeStack = new Stack<>();
            patternNodeStack.push(new Conjunction());
        }

        /**
         * Create a new disjunction at the current cursor position in the pattern and place the cursor in its first
         * clause.
         */
        public void enterDisjunction() {
            Disjunction disjunction = new Disjunction();
            Conjunction firstClause = new Conjunction();

            disjunction.add(firstClause);

            patternNodeStack.peek().add(disjunction);
            patternNodeStack.push(disjunction);
            patternNodeStack.push(firstClause);
        }

        /**
         * Create a new clause for the current disjunction and move to cursor into it.
         */
        public void continueDisjunction() {
            if (patternNodeStack.size() < 3) {
                throw new IllegalStateException("Not in a disjunction");
            }

            if (patternNodeStack.peek().size() > 0) {
                Conjunction nextClause = new Conjunction();
                patternNodeStack.pop();
                patternNodeStack.peek().add(nextClause);
                patternNodeStack.push(nextClause);
            }
        }

        /**
         * Exit the current disjunction, moving the cursor back out into the enclosing pattern element.
         */
        public void exitDisjunction() {
            if (patternNodeStack.size() < 3) {
                throw new IllegalStateException("Not in a disjunction");
            }

            Conjunction lastClause = (Conjunction) patternNodeStack.pop();
            Disjunction disjunction = (Disjunction) patternNodeStack.pop();

            if (lastClause.size() == 0) {
                disjunction.remove(lastClause);
            }

            if (disjunction.size() == 0) {
                patternNodeStack.peek().remove(disjunction);
            }
        }

        /**
         * Add a String at the current position of the cursor.
         *
         * @param s String to add
         */
        public void addString(String s) {
            patternNodeStack.peek().add(new _String(s));
        }

        /**
         * Check if the Pattern matches a given String. The target String is considered to match if all Strings
         * required by the Pattern are present, taking disjunctions under account.
         *
         * @param s String to match
         * @return True if the String matches the Pattern, false otherwise
         */
        public boolean matches(String s) {
            return patternNodeStack.peek().matches(s.toLowerCase());
        }

        @Override
        public String toString() {
            return patternNodeStack.peek().toString();
        }

        /**
         * Stack of pattern nodes, top of stack serves as cursor position.
         */
        private Stack<PatternNode> patternNodeStack;
    }
}
