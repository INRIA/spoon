package spoon.smpl.formula;

/**
 * Optional(phi) represents a shorthand of the construction "Or(phi, Not(phi))" that can be handled more
 * efficiently by taking the result to be X union Y, where
 *    X = SAT(phi)
 *    Y = {(s, e, w) in SAT(True) | (s, _, _) not in X}
 */
public class Optional extends UnaryConnective {
    /**
     * Create a new "Or(phi, Not(phi))" logical connective.
     *
     * @param phi Formula
     */
    public Optional(Formula phi)
    {
        super(phi);
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
        return "Optional(" + getInnerElement().toString() + ")";
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Optional && other.hashCode() == hashCode());
    }
}
