package spoon.smpl;

import spoon.smpl.formula.Predicate;

import java.util.List;

/**
 * A Label is a piece of arbitrary information associated with a state of a CTL model.
 */
public interface Label {
	/**
	 * Test whether the label matches the given predicate.
	 *
	 * @param obj Predicate to test
	 * @return True if the label matches the predicate, false otherwise.
	 */
	boolean matches(Predicate obj);

	/**
	 * Get the match results produced for the most recently matched Predicate.
	 *
	 * @return List of results
	 */
	List<LabelMatchResult> getMatchResults();

	/**
	 * Reset/clear metavariable bindings
	 */
	void reset();
}
