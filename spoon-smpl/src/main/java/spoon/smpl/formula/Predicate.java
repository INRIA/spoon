package spoon.smpl.formula;

import java.util.Map;

/**
 * A Predicate is a Formula that can match state labels of a CTL model.
 * <p>
 * Semantically, the set of states that satisfy a Predicate are the states for which the
 * predicate matches one or more of the states' labels.
 */
public interface Predicate extends Formula {
	/**
	 * Get the metavariables (and their constraints) associated with the predicate.
	 *
	 * @return Metavariables
	 */
	Map<String, MetavariableConstraint> getMetavariables();

	/**
	 * Validate and potentially modify metavariable bindings.
	 *
	 * @param parameters Mutable map of metavariable bindings
	 * @return True if bindings could be validated (potentially by modification), false otherwise
	 */
	boolean processMetavariableBindings(Map<String, Object> parameters);
}
