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
    public StatementPattern(PatternNode pattern, Map<String, MetavariableConstraint> metavars) {
        this.stringRep = "???";
        this.pattern = pattern;
        this.metavars = metavars;
    }

    /**
     * Set the string representation for the statement, to be used in toString.
     * @param stringRep String representation of statement
     */
    public void setStringRepresentation(String stringRep) {
        this.stringRep = stringRep;
    }

    /**
     * Get the string representation of the statement.
     * @return String representation of statement
     */
    public String getStringRepresentation() {
        return stringRep;
    }

    /**
     * Get the pattern to match.
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

    /**
     * Get the metavariables (and their constraints) associated with the predicate.
     * @return Metavariable names and their respective constraints
     */
    @Override
    public Map<String, MetavariableConstraint> getMetavariables() {
        return metavars;
    }

    /**
     * Validate and potentially modify metavariable bindings.
     * @param parameters Mutable map of metavariable bindings
     * @return True if bindings could be validated (potentially by modification), false otherwise
     */
    @Override
    public boolean processMetavariableBindings(Map<String, Object> parameters) {
        if (metavars == null) {
            return true;
        }

        for (String key : metavars.keySet()) {
            if (parameters.containsKey(key)) {
                Object result = metavars.get(key).apply(parameters.get(key));

                if (result != null) {
                    parameters.put(key, result);
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Statement(").append(getStringRepresentation()).append(")");
        return sb.toString();
    }

    /**
     * String representation of statement.
     */
    private String stringRep;

    /**
     * The Pattern to match.
     */
    private PatternNode pattern;

    /**
     * Metavariable names and their respective constraints.
     */
    private Map<String, MetavariableConstraint> metavars;
}
