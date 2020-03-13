package spoon.smpl.formula;

import spoon.pattern.Pattern;

/**
 * A StatementPattern is a Predicate that contains a Spoon Pattern.
 *
 * The intention is for a StatementPattern to contain a Pattern that
 * corresponds to a Java statement, but the current implementation
 * does not enforce this.
 */
public class StatementPattern implements Predicate {
    /**
     * Create a new StatementPattern Predicate.
     * @param pattern The pattern to match
     */
    public StatementPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * @return The Pattern to match
     */
    public Pattern getPattern() {
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

    /**
     * The Pattern to match.
     */
    private Pattern pattern;
}
