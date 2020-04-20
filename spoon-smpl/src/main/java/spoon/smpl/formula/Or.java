package spoon.smpl.formula;

/**
 * Or represents the classical "OR" logical connective.
 */
public class Or extends BinaryConnective {
    /**
     * Create a new "OR" logical connective.
     *
     * @param lhs First Formula that suffices
     * @param rhs Second Formula that suffices
     */
    public Or(Formula lhs, Formula rhs) {
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
        return "Or(" + getLhs().toString() + ", " + getRhs().toString() + ")";
    }
}
