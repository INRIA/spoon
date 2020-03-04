package spoon.smpl.formula;

/**
 * A Proposition is a constant, unparameterized predicate. The actual proposition is represented
 * by a given string.
 */
public class Proposition implements Predicate {
    /**
     * Create a new Proposition.
     * @param proposition The proposition string
     */
    public Proposition(String proposition) {
        this.proposition = proposition;
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
     * Get the proposition string.
     * @return The proposition string
     */
    public String getProposition() {
        return proposition;
    }

    /**
     * @return a string representation of this element
     */
    @Override
    public String toString() {
        return proposition;
    }

    /**
     * The proposition string.
     */
    private String proposition;
}
