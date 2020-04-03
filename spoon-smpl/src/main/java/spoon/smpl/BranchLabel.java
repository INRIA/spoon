package spoon.smpl;

import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtElement;
import spoon.smpl.formula.BranchPattern;
import spoon.smpl.formula.Predicate;
import spoon.smpl.pattern.PatternBuilder;
import spoon.smpl.pattern.PatternMatcher;
import spoon.smpl.pattern.PatternNode;

import java.util.Map;

/**
 * A BranchLabel is a Label used to associate states with CtElement code
 * elements that can be matched using BranchPattern Formula elements.
 */
public class BranchLabel implements Label {
    /**
     * Create a new BranchLabel.
     * @param cond Condition element of a branch statement element such as CtIf
     */
    public BranchLabel(CtElement cond) {
        CtElement parent = cond.getParent(); // throws ParentNotInitializedException

        boolean ok = false;

        // check for supported type of branch element, more supported types to come
        if (parent instanceof CtIf) {
            ok = true;
        }

        if (!ok) {
            throw new IllegalArgumentException("Invalid condition parent");
        }

        this.cond = cond;

        PatternBuilder builder = new PatternBuilder();
        cond.accept(builder);
        this.condPattern = builder.getResult();

        this.metavarBindings = null;
    }

    /**
     * Test whether the label matches the given predicate.
     * @param obj Predicate to test
     * @return True if the predicate is a StatementPattern element whose Pattern matches the code exactly once, false otherwise.
     */
    public boolean matches(Predicate obj) {
        if (obj instanceof BranchPattern) {
            BranchPattern bp = (BranchPattern) obj;

            if (!bp.getBranchType().isInstance(cond.getParent())) {
                return false;
            }

            PatternMatcher matcher = new PatternMatcher(bp.getConditionPattern());
            condPattern.accept(matcher);
            metavarBindings = matcher.getParameters();
            return matcher.getResult() && bp.processMetavariableBindings(metavarBindings);
        } else {
            return false;
        }
    }

    /**
     * Retrieve any metavariable bindings involved in matching the most recently
     * given predicate.
     * @return most recent metavariable bindings, or null if there were no bindings
     */
    @Override
    public Map<String, Object> getMetavariableBindings() {
        return metavarBindings;
    }

    /**
     * Reset/clear metavariable bindings
     */
    @Override
    public void reset() {
        metavarBindings = null;
    }

    /**
     * Get the code element.
     * @return The code element
     */
    public CtElement getCondition() {
        return cond;
    }

    @Override
    public String toString() {
        return "if (" + cond.toString() + ")";
    }

    /**
     * The branch condition element.
     */
    private CtElement cond;

    /**
     * The pattern corresponding to the branch condition element.
     * Part of temporary substitute for spoon.pattern.
     */
    private PatternNode condPattern;

    private Map<String, Object> metavarBindings;
}
