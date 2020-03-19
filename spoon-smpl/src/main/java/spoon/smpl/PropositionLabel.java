package spoon.smpl;

import spoon.smpl.formula.Predicate;
import spoon.smpl.formula.Proposition;

import java.util.Map;

/**
 * A PropositionLabel is a Label used to associate states with simple, constant proposition strings
 * that can be matched using Proposition Formula elements.
 */
public class PropositionLabel implements Label {
    /**
     * Create a new PropositionLabel.
     * @param label The proposition string
     */
    public PropositionLabel(String label) {
        this.label = label;
    }

    /**
     * Test whether the label matches the given predicate.
     * @param obj Predicate to test
     * @return True if the predicate is a Proposition element with matching proposition string, false otherwise.
     */
    public boolean matches(Predicate obj) {
        if (obj instanceof Proposition) {
            return ((Proposition)obj).getProposition().equals(label);
        } else {
            return false;
        }
    }

    @Override
    public Map<String, Object> getMatchedParameters() {
        return null;
    }

    @Override
    public void reset() {

    }

    /**
     * The proposition string.
     */
    private String label;
}
