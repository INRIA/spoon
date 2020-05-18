package spoon.smpl.formula;

import spoon.reflect.declaration.CtMethod;
import spoon.smpl.MethodHeaderModel;

import java.util.*;

/**
 * A MethodHeaderPredicate contains an inner formula suitable for matching against a MethodHeaderModel.
 */
public class MethodHeaderPredicate extends ParameterizedPredicate {
    /**
     * Create a new MethodHeaderPredicate given a method and a set of metavariable declarations.
     *
     * @param method Method defining the header signature that is to be matched by this predicate
     * @param metavars Metavariable names and their corresponding constraints
     */
    public MethodHeaderPredicate(CtMethod<?> method, Map<String, MetavariableConstraint> metavars) {
        super(metavars);
        metavarsUsedInHeader = new HashSet<>();
        headerFormula = MethodHeaderModel.compileMethodHeaderFormula(method, metavars, metavarsUsedInHeader);
    }

    /**
     * Get the header-matching inner formula.
     *
     * @return Formula for matching a MethodHeaderModel
     */
    public Formula getHeaderFormula() {
        return headerFormula;
    }

    /**
     * Get the set of metavariable names involved in the header-matching inner formula. An enclosing
     * Formula should quantify these.
     *
     * @return Set of metavariable names involved in the header-matching inner formula
     */
    public Set<String> getMetavarsUsedInHeader() {
        return metavarsUsedInHeader;
    }

    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "MethodHeader(" + headerFormula.toString() + ")";
    }

    /**
     * The header-matching inner formula.
     */
    private final Formula headerFormula;

    /**
     * Set of metavariable names involved in the header-matching inner formula.
     */
    private final Set<String> metavarsUsedInHeader;
}
