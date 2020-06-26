package spoon.smpl;

import spoon.reflect.declaration.CtElement;

import java.util.Map;

/**
 * A LabelMatchResult is a record of a match between a Predicate and a Label, recording the specific (sub-)element
 * that matched and any metavariable bindings involved in establishing the match.
 */
public interface LabelMatchResult {
    /**
     * Get the specifically matched code (sub-)element of the matching Label, if applicable.
     *
     * @return Matched code (sub-)element, or null if not applicable
     */
    CtElement getMatchedElement();

    /**
     * Get the metavariable bindings involved in the match, if any.
     *
     * @return Metavariable bindings, or null if there are none
     */
    Map<String, Object> getMetavariableBindings();
}
