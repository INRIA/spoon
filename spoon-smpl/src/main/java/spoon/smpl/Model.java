package spoon.smpl;

import java.util.List;

/**
 * Model defines the base interface of a CTL model.
 */
public interface Model {
	/**
	 * Get the set of states.
	 *
	 * @return The set of states
	 */
	List<Integer> getStates();

	/**
	 * Get the set of immediately adjacent successors to a given state.
	 *
	 * @param state Parent state
	 * @return Set of immediately adjacent successors
	 */
	List<Integer> getSuccessors(int state);

	/**
	 * Get the set of labels associated with a given state.
	 *
	 * @param state Target state
	 * @return Set of labels associated with target state
	 */
	List<Label> getLabels(int state);
}
