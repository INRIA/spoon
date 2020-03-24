package spoon.smpl.formula;

//import spoon.pattern.Pattern;
import spoon.smpl.pattern.PatternNode;

import java.util.Map;

/**
 * A StatementPattern is a Predicate that contains a Spoon Pattern. (temporarily
 * substituted for an internal pattern matching mechanism)
 *
 * The intention is for a StatementPattern to contain a Pattern that
 * corresponds to a Java statement, but the current implementation
 * does not enforce this.
 */
public class StatementPattern implements Predicate {
    public StatementPattern(PatternNode pattern) {
        this(pattern, null);
    }
    /**
     * Create a new StatementPattern Predicate.
     * @param pattern The pattern to match
     */
    public StatementPattern(PatternNode pattern, Map<String, ParameterPostProcessStrategy> paramStrats) {
        this.pattern = pattern;
        this.paramStrats = paramStrats;
    }

    /**
     * @return The Pattern to match
     */
    public PatternNode getPattern() {
        return pattern;
    }

    /**
     * Implements the Visitor pattern.
     * @param visitor
     */
    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean processParameterBindings(Map<String, Object> parameters) {
        if (paramStrats == null) {
            return true;
        }

        for (String key : paramStrats.keySet()) {
            if (parameters.containsKey(key) && !paramStrats.get(key).apply(parameters, key)) {
                return false;
            }
        }

        return true;
    }

    /**
     * The Pattern to match.
     */
    private PatternNode pattern;

    private Map<String, ParameterPostProcessStrategy> paramStrats;
}
