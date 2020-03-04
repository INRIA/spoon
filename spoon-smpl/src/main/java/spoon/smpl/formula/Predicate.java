package spoon.smpl.formula;

/**
 * A Predicate is a Formula that can appear in or match things from the set of state labels of a CTL model.
 *
 * Semantically, the set of states that satisfy a Predicate are the states for which the
 * predicate matches one or more of the states' labels.
 */
public interface Predicate extends Formula {
}
