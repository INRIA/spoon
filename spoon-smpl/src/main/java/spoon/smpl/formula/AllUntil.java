package spoon.smpl.formula;

/**
 * AllUntil represents the AU logical connective of CTL.
 *
 * Semantically, "A[p U q]" (or "p AU q") selects the states where "p" holds
 * on every step along every possible path until "q" eventually holds.
 */
public class AllUntil extends BinaryConnective {
    /**
     * Create a new AU logical connective.
     * @param lhs The Formula that must hold until the second one does
     * @param rhs The Formula that must eventually hold
     */
    public AllUntil(Formula lhs, Formula rhs)
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
        return "AU(" + getLhs().toString() + ", " + getRhs().toString() + ")";
    }
}
