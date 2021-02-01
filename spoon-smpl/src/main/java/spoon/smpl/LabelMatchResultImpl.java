package spoon.smpl;

import spoon.reflect.declaration.CtElement;

import java.util.Map;

/**
 * A LabelMatchResultImpl is a record of a match between a Predicate and a Label, recording the specific (sub-)element
 * that matched and any metavariable bindings involved in establishing the match.
 */
public class LabelMatchResultImpl implements LabelMatchResult {
	/**
	 * Create a new LabelMatchResult that records no further information.
	 */
	public LabelMatchResultImpl() {
		this(null, null);
	}

	/**
	 * Create a new LabelMatchResult that records metavariable bindings.
	 *
	 * @param metavarBindings Metavariable bindings
	 */
	public LabelMatchResultImpl(Map<String, Object> metavarBindings) {
		this(null, metavarBindings);
	}

	/**
	 * Create a new LabelMatchResult with a specified matching code element and metavariable bindings.
	 *
	 * @param matchedElement  Matching code element
	 * @param metavarBindings Metavariable bindings
	 */
	public LabelMatchResultImpl(CtElement matchedElement, Map<String, Object> metavarBindings) {
		this.matchedElement = matchedElement;
		this.metavarBindings = metavarBindings;
	}

	/**
	 * Get the specifically matched code (sub-)element of the matching Label, if applicable.
	 *
	 * @return Matched code (sub-)element, or null if not applicable
	 */
	@Override
	public CtElement getMatchedElement() {
		return matchedElement;
	}

	/**
	 * Get the metavariable bindings involved in the match, if any.
	 *
	 * @return Metavariable bindings, or null if there are none
	 */
	@Override
	public Map<String, Object> getMetavariableBindings() {
		return metavarBindings;
	}

	/**
	 * Matched code (sub-)element, potentially null.
	 */
	private final CtElement matchedElement;

	/**
	 * Metavariable bindings, potentially null.
	 */
	private final Map<String, Object> metavarBindings;
}
