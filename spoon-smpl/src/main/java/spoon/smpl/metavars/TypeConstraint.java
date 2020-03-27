package spoon.smpl.metavars;

import spoon.reflect.reference.CtTypeReference;
import spoon.smpl.formula.MetavariableConstraint;

/**
 * A TypeConstraint restricts a metavariable binding to be a CtTypeReference.
 */
public class TypeConstraint implements MetavariableConstraint {
    /**
     * Validate and potentially modify a value bound to a metavariable.
     * @param value Value bound to metavariable
     * @return The Object that is a valid binding under the constraint, or null if the value does not match the constraint
     */
    @Override
    public Object apply(Object value) {
        if (value instanceof CtTypeReference) {
            return value;
        } else {
            return null;
        }
    }
}
