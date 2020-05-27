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
     * Create an OR between two given formulae, but only if the second formula is not null.
     *
     * @param left Left Formula
     * @param right Right Formula
     * @return OR(Left, Right) if the right Formula is not null, otherwise the left Formula.
     */
    public static Formula connectIfNotNull(Formula left, Formula right) {
        if (left == null) {
            throw new IllegalArgumentException("left Formula must not be null");
        }

        if (right != null) {
            return new Or(left, right);
        } else {
            return left;
        }
    }

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
        return "Or(" + getLhs().toString() + ", " + getRhs().toString() + ")";
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Or && other.hashCode() == hashCode());
    }
}
