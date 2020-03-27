package spoon.smpl.metavars;

import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.reference.CtVariableReference;
import spoon.smpl.formula.MetavariableConstraint;

/**
 * An IdentifierConstraint restricts a metavariable binding to be CtVariableReference, potentially
 * by refining a given binding to a CtVariableRead or CtVariableWrite.
 */
public class IdentifierConstraint implements MetavariableConstraint {
    /**
     * Validate and potentially modify a value bound to a metavariable.
     * @param value Value bound to metavariable
     * @return The Object that is a valid binding under the constraint, or null if the value does not match the constraint
     */
    @Override
    public Object apply(Object value) {
        if (value instanceof CtVariableReference) {
            return value;
        } else if (value instanceof CtVariableRead) {
            return ((CtVariableRead<?>) value).getVariable();
        } else if (value instanceof CtVariableWrite) {
            return ((CtVariableWrite<?>) value).getVariable();
        } else {
            return null;
        }
    }
}
