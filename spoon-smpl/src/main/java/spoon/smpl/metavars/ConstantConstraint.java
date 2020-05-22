package spoon.smpl.metavars;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtElement;
import spoon.smpl.formula.MetavariableConstraint;

/**
 * A ConstantConstraint restricts a metavariable binding to be a CtLiteral, potentially
 * by refining a given binding to a CtExpression if the expression consists of a single
 * CtLiteral.
 */
public class ConstantConstraint implements MetavariableConstraint {
    /**
     * Validate and potentially modify a value bound to a metavariable.
     * @param value Value bound to metavariable
     * @return The Object that is a valid binding under the constraint, or null if the value does not match the constraint
     */
    @Override
    public CtElement apply(CtElement value) {
        if (value instanceof CtLiteral) {
            return value;
        } else if (value instanceof CtExpression) {
            CtExpression<?> expr = (CtExpression<?>) value;

            if (expr.getDirectChildren().get(0) instanceof CtLiteral) {
                return expr.getDirectChildren().get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
