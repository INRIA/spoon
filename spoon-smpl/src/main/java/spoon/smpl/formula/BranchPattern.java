package spoon.smpl.formula;

//import spoon.pattern.Pattern;
import spoon.reflect.declaration.CtElement;
import spoon.smpl.pattern.PatternNode;

import java.util.Map;

/**
 * A BranchPattern is a Predicate that contains a Spoon Pattern (temporarily
 * substituted for an internal pattern matching mechanism) and a type identifier
 * indicating the type of the branch statement element (e.g CtIf.class)
 *
 * The intention is for a BranchPattern to contain a Pattern that corresponds to
 * the conditional expression used in a given branch statement element such as CtIf,
 * but the current implementation does not enforce this.
 */
public class BranchPattern implements Predicate {
    public BranchPattern(PatternNode cond, Class<? extends CtElement> branchType) {
        this(cond, branchType, null);
    }
    /**
     * Create a new BranchPattern Predicate.
     * @param cond The pattern to match (against the condition)
     */
    public BranchPattern(PatternNode cond, Class<? extends CtElement> branchType, Map<String, MetavariableConstraint> metavars) {
        this.cond = cond;
        this.branchType = branchType;
        this.metavars = metavars;
        this.stringRep = "???";
    }

    /**
     * Set the string representation for the branch condition, to be used in toString.
     * @param stringRep String representation of branch condition
     */
    public void setStringRepresentation(String stringRep) {
        this.stringRep = stringRep;
    }

    /**
     * Get the string representation of the branch condition.
     * @return String representation of branch condition
     */
    public String getStringRepresentation() {
        return stringRep;
    }

    /**
     * Get the branch condition pattern.
     * @return the Pattern to match
     */
    public PatternNode getConditionPattern() {
        return cond;
    }

    /**
     * Get the branch type.
     * @return the type of the branch statement element
     */
    public Class<? extends CtElement> getBranchType() {
        return branchType;
    }

    /**
     * Implements the Visitor pattern.
     * @param visitor Visitor to accept
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
        sb.append("Branch<").append(branchType.getSimpleName()).append(">(").append(getStringRepresentation()).append(")");
        return sb.toString();
    }

    /**
     * String representation of branch condition.
     */
    private String stringRep;

    /**
     * The Pattern to match.
     */
    private PatternNode cond;

    /**
     * The type of the branch statement element.
     */
    private Class<? extends CtElement> branchType;

    /**
     * Metavariable names and their respective constraints.
     */
    private Map<String, MetavariableConstraint> metavars;
}
