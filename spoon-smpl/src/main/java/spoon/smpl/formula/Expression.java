package spoon.smpl.formula;

import spoon.reflect.declaration.CtElement;

import java.util.Map;

/**
 * An Expression Predicate contains a parameterized match pattern for an expression.
 */
public class Expression extends CodeElementPredicate {
    public Expression(CtElement codeElement) { super(codeElement); }

    /**
     * Create a new Expression Predicate.
     *
     * @param codeElement Expression code element
     * @param metavars Metavariable names and their corresponding constraints
     */
    public Expression(CtElement codeElement, Map<String, MetavariableConstraint> metavars) { super(codeElement, metavars); }

    /**
     * Implements the Visitor pattern.
     * @param visitor
     */
    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Expression(").append(getCodeElementStringRepresentation()).append(")");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Expression && other.hashCode() == hashCode());
    }
}
