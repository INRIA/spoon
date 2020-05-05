package spoon.smpl.formula;

import java.util.function.Function;

/**
 * A MetavariableConstraint is a function that validates and potentially modifies a
 * value that is bound to (or is in the process of being bound to) a metavariable.
 */
public interface MetavariableConstraint extends Function<Object, Object> {
    /**
     * Validate and potentially modify a value bound to a metavariable.
     *
     * @param value Value bound to metavariable
     * @return The Object that is a valid binding under the constraint, or null if the value does not match the constraint
     */
    @Override
    public Object apply(Object value);
}
