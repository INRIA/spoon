package spoon.smpl.formula;

import java.util.Map;

/**
 * A VariableUsePredicate is a Predicate that should match code element labels whose code
 * elements either involve access to an explicitly given variable name or involve access
 * to a variable that can be bound to a metavariable specified by the predicate.
 *
 * VariableUsePredicate("x", {}) should match all states in a model where a variable or
 * field "x" is accessed in any way.
 *
 * VariableUsePredicate("v1", {v1: IdentifierConstraint}) should match all states in a
 * model where a variable is used that can be bound to the metavariable "v1" according to
 * its constraints.
 */
public class VariableUsePredicate implements Predicate {
    /**
     * Create a new VariableUsePredicate using a given variable (or metavariable) name and
     * metavariable spec.
     *
     * @param variable Variable (or metavariable) name
     * @param metavars Metavariable names and their corresponding constraints
     */
    public VariableUsePredicate(String variable, Map<String, MetavariableConstraint> metavars) {
        this.variable = variable;
        this.metavars = metavars;
    }

    /**
     * Get the variable (or metavariable) name to search for.
     *
     * @return Variable (or metavariable) name
     */
    public String getVariable() {
        return variable;
    }

    @Override
    public Map<String, MetavariableConstraint> getMetavariables() {
        return metavars;
    }

    /**
     * Validate and potentially modify metavariable bindings.
     *
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
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Used(" + variable + ")";
    }

    /**
     * Variable (or metavariable) name to match.
     */
    private String variable;

    /**
     * Metavariable names and their corresponding constraints.
     */
    private Map<String, MetavariableConstraint> metavars;
}
