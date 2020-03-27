package spoon.smpl.metavars;

import spoon.reflect.code.CtExpression;
import spoon.smpl.formula.MetavariableConstraint;

/**
 * An ExpressionConstraint restricts a metavariable binding to be a CtExpression.
 */
public class ExpressionConstraint implements MetavariableConstraint {
    /**
     * Validate and potentially modify a value bound to a metavariable.
     * @param value Value bound to metavariable
     * @return The Object that is a valid binding under the constraint, or null if the value does not match the constraint
     */
    @Override
    public Object apply(Object value) {
        if (value instanceof CtExpression) {
            return value;
        } else {
            return null;
        }
    }
}
