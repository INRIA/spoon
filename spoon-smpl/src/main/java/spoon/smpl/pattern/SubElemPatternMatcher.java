package spoon.smpl.pattern;

import spoon.reflect.declaration.CtElement;

import java.util.HashMap;
import java.util.Map;

/**
 * A SubElemPatternMatcher is a PatternNode with the added ability of matching a given "rule" pattern against
 * all ElemNode sub-patterns contained in a "target" pattern.
 */
public class SubElemPatternMatcher {
    /**
     * Create a new SubElemPatternMatcher using the given rule pattern that should be allowed to match any ElemNode
     * sub-pattern of a target pattern.
     *
     * @param rulePattern Rule pattern
     */
    public SubElemPatternMatcher(PatternNode rulePattern) {
        this.rulePattern = rulePattern;
        reset();
    }

    /**
     * Reset the matcher state.
     */
    public void reset() {
        parameters = new HashMap<>();
        matchedElement = null;
    }

    /**
     * Test if the rule pattern matches a given target pattern or any of its ElemNode sub-patterns.
     *
     * @param pattern Target pattern
     * @return True if there is any match, false otherwise
     */
    public boolean matches(PatternNode pattern) {
        DotsExtPatternMatcher matcher = new DotsExtPatternMatcher(rulePattern);
        SubElemPatternCollector spc = new SubElemPatternCollector();
        pattern.accept(spc);

        for (ElemNode subPattern : spc.getResult()) {
            subPattern.accept(matcher);

            if (matcher.getResult() == true) {
                parameters = matcher.getParameters();
                matchedElement = subPattern.elem;
                return true;
            }

            matcher.reset();
        }

        return false;
    }

    /**
     * Get parameter bindings of most recent successful pattern match, if any.
     *
     * @return Parameters matched in most recent successful pattern match
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * Get the element corresponding to the specific ElemNode that yielded a match.
     *
     * @return The element corresponding to the specific ElemNode that matched
     */
    public CtElement getMatchedElement() {
        return matchedElement;
    }

    /**
     * Rule pattern.
     */
    private PatternNode rulePattern;

    /**
     * Parameter bindings of most recent successful pattern match.
     */
    private Map<String, Object> parameters;

    /**
     * Element corresponding to specific ElemNode that yielded the most recent successful match.
     */
    private CtElement matchedElement;
}
