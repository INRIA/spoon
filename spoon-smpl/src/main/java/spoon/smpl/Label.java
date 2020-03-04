package spoon.smpl;

import spoon.smpl.formula.Predicate;

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
}
