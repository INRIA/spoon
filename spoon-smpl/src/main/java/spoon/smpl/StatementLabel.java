package spoon.smpl;

//import spoon.pattern.Match;
import spoon.reflect.declaration.CtElement;
import spoon.smpl.formula.StatementPattern;
import spoon.smpl.formula.Predicate;
import spoon.smpl.pattern.*;

import java.util.Map;

/**
 * A StatementLabel is a Label used to associate states with CtElement code
 * elements that can be matched using StatementPattern Formula elements.
 *
 * The intention is for a StatementLabel to contain code that corresponds
 * to a Java statement, but the current implementation does not enforce this.
 */
public class StatementLabel implements Label {
    /**
     * Create a new StatementLabel.
     * @param code Code element
     */
    public StatementLabel(CtElement code) {
        this.code = code;

        PatternBuilder builder = new PatternBuilder();
        code.accept(builder);
        this.codePattern = builder.getResult();

        this.metavarBindings = null;
    }

    /**
     * Test whether the label matches the given predicate.
     * @param obj Predicate to test
     * @return True if the predicate is a StatementPattern element whose Pattern matches the code, false otherwise.
     */
    public boolean matches(Predicate obj) {
        if (obj instanceof StatementPattern) {
            StatementPattern sp = (StatementPattern) obj;
            PatternMatcher matcher = new PatternMatcher(sp.getPattern());
            codePattern.accept(matcher);
            metavarBindings = matcher.getParameters();
            return matcher.getResult() && sp.processMetavariableBindings(metavarBindings);
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
    public CtElement getStatement() {
        return code;
    }

    @Override
    public String toString() {
        return code.toString();
    }

    /**
     * The code element.
     */
    private CtElement code;

    /**
     * The pattern corresponding to the code element.
     * Part of temporary substitute for spoon.pattern.
     */
    private PatternNode codePattern;

    private Map<String, Object> metavarBindings;
}
