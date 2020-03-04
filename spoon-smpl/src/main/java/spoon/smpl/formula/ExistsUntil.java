package spoon.smpl.formula;

/**
 * ExistsUntil represents the EU logical connective of CTL.
 *
 * Semantically, "E[p U q]" (or "p EU q") selects the states for which there is at least
 * one path where "p" holds on every step until "q" eventually holds.
 */
public class ExistsUntil extends BinaryConnective {
    /**
     * Create a new EU logical connective.
     * @param lhs The Formula that must hold until the second one does
     * @param rhs The Formula that must eventually hold
     */
    public ExistsUntil(Formula lhs, Formula rhs)
    {
        super(lhs, rhs);
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
     * @return a string representation of this element and its children
     */
    @Override
    public String toString() {
        return "E[" + getLhs().toString() + " U " + getRhs().toString() + "]";
    }
}
