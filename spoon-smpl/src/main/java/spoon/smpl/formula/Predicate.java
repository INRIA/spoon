package spoon.smpl.formula;

import spoon.reflect.declaration.CtElement;

import java.util.Map;

/**
 * A Predicate is a Formula that can match state labels of a CTL model.
 *
 * Semantically, the set of states that satisfy a Predicate are the states for which the
 * predicate matches one or more of the states' labels.
 */
public interface Predicate extends Formula {
    /**
     * Get the metavariables (and their constraints) associated with the predicate.
     *
     * @return Metavariables
     */
    public Map<String, MetavariableConstraint> getMetavariables();

    /**
     * Validate and potentially modify metavariable bindings.
     *
     * @param parameters Mutable map of metavariable bindings
     * @return True if bindings could be validated (potentially by modification), false otherwise
     */
    public boolean processMetavariableBindings(Map<String, Object> parameters);

    /**
     * Set the specific (sub-)element that matched the Predicate.
     *
     * @param element Element that matched the Predicate
     */
    public void setMatchedElement(CtElement element);

    /**
     * Check if the Predicate should be seen as having bound a specific matching (sub-)element rather than having
     * matched a full CFG statement node.
     *
     * @return True if the Predicate has bound a specific matching (sub-)element, false otherwise
     */
    public boolean hasMatchedElement();

    /**
     * Get the specific (sub-)element that matched the Predicate, if any.
     *
     * @return The specific (sub-)element that matched the Predicate, or null if the Predicate matched a full CFG
     *         statement node
     */
    public CtElement getMatchedElement();
}
