package spoon.smpl.formula;

import spoon.reflect.declaration.CtElement;

import java.util.Map;
import java.util.Stack;

/**
 * ParameterizedPredicate provides an abstract base class for Predicates that can bind
 * metavariables.
 */
public abstract class ParameterizedPredicate implements Predicate {
	/**
	 * Create a new parameterized Predicate using a given set of metavariables.
	 *
	 * @param metavars Metavariable names and their corresponding constraints
	 */
	public ParameterizedPredicate(Map<String, MetavariableConstraint> metavars) {
		this.metavars = metavars;
		this.matchedElement = new Stack<>();
	}

	/**
	 * Get the metavariables (and their constraints) associated with the predicate.
	 *
	 * @return Metavariable names and their respective constraints
	 */
	@Override
	public Map<String, MetavariableConstraint> getMetavariables() {
		return metavars;
	}

	/**
	 * Validate and potentially modify metavariable bindings.
	 *
	 * @param parameters Mutable map of metavariable bindings
	 * @return True if bindings could be validated (potentially by modification), false otherwise
	 */
	@Override
	public boolean processMetavariableBindings(Map<String, Object> parameters) {
		if (metavars == null) {
			return true;
		}

		for (String key : metavars.keySet()) {
			if (parameters.containsKey(key)) {
				CtElement result = metavars.get(key).apply((CtElement) parameters.get(key));

				if (result != null) {
					parameters.put(key, result);
				} else {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || (other.getClass().equals(getClass()) && other.hashCode() == hashCode());
	}

	/**
	 * Metavariable names and their corresponding constraints.
	 */
	private Map<String, MetavariableConstraint> metavars;

	/**
	 * Stack of matching (sub-)elements.
	 */
	private Stack<CtElement> matchedElement;
}
