package spoon.smpl.metavars;

import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtVariableReference;
import spoon.smpl.formula.MetavariableConstraint;

/**
 * An IdentifierConstraint restricts a metavariable binding to be CtVariableReference, potentially
 * by refining a given binding to a CtVariableAccess
 */
public class IdentifierConstraint implements MetavariableConstraint {
	/**
	 * Validate and potentially modify a value bound to a metavariable.
	 *
	 * @param value Value bound to metavariable
	 * @return The Object that is a valid binding under the constraint, or null if the value does not match the constraint
	 */
	@Override
	public CtElement apply(CtElement value) {
		if (value instanceof CtVariableReference) {
			return value;
		} else if (value instanceof CtVariableAccess) {
			return ((CtVariableAccess<?>) value).getVariable();
		} else {
			return null;
		}
	}
}
