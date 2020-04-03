package spoon.smpl.formula;

/**
 * ExistsNext represents the existentially quantified variable logical connective of CTL-V(W).
 *
 * Semantically, "Ev(p)" selects the states that satisfy the formula "p" while also removing
 * the binding for the metavariable "v" from the environment, if such a binding exists.
 */
public class ExistsVar implements Formula {
    /**
     * Create a new existentially quantified variable logical connective.
     * @param varName Variable name
     * @param innerElement The Formula that should hold in some successor
     */
    public ExistsVar(String varName, Formula innerElement)
    {
        this.varName = varName;
        this.innerElement = innerElement;
    }

    public String getVarName() {
        return varName;
    }

    public Formula getInnerElement() {
        return innerElement;
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
        return "E" + varName + "(" + innerElement.toString() + ")";
    }

    private String varName;
    private Formula innerElement;
}
