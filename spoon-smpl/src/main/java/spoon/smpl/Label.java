package spoon.smpl;

import spoon.smpl.formula.Predicate;

import java.util.Map;

/**
 * A Label is a piece of arbitrary information associated with a state of a CTL model.
 */
public interface Label {
    /**
     * Test whether the label matches the given predicate.
     * @param obj Predicate to test
     * @return True if the label matches the predicate, false otherwise.
     */
    public boolean matches(Predicate obj);

    /**
     * Retrieve the set of metavariable bindings involved in matching the most recently
     * given predicate.
     * @return most recent metavariable bindings, or null if there were no bindings
     */
    public Map<String, Object> getMetavariableBindings();

    /**
     * Reset/clear metavariable bindings
     */
    public void reset();
}
