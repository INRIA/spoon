package spoon.smpl.metavars;

import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtVariableReference;
import spoon.smpl.formula.MetavariableConstraint;

/**
 * An IdentifierConstraint restricts a metavariable binding to be CtVariableReference, potentially
 * by refining a given binding to a CtVariableRead, CtVariableWrite or CtVariable
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
        } else if (value instanceof CtFieldAccess) {
            return null;
        } else if (value instanceof CtVariableAccess) {
            return ((CtVariableAccess<?>) value).getVariable();
        } else if (value instanceof CtVariable) {
            CtVariable ctVariable = (CtVariable) value;
            CtVariableReference ref = ctVariable.getFactory().createLocalVariableReference();
            ref.setType(ctVariable.getType());
            ref.setSimpleName(ctVariable.getSimpleName());
            return ref;
        } else {
            return null;
        }
    }
}
