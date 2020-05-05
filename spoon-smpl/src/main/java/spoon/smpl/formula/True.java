package spoon.smpl.formula;

/**
 * True represents a logical constant truth.
 *
 * In the semantics of CTL, "True" simply selects all states.
 */
public class True implements Formula {
    /**
     * Implements the Visitor pattern.
     *
     * @param visitor Visitor to accept
     */
    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "T";
    }
}
