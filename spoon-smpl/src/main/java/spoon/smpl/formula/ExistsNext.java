package spoon.smpl.formula;

/**
 * ExistsNext represents the EX logical connective of CTL.
 *
 * Semantically, "EX p" selects the states for which at least one successor satisfies "p".
 */
public class ExistsNext extends UnaryConnective {
    /**
     * Create a new EX logical connective.
     *
     * @param innerElement The Formula that should hold in some successor
     */
    public ExistsNext(Formula innerElement)
    {
        super(innerElement);
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
        return "EX(" + getInnerElement().toString() + ")";
    }
}
