package spoon.smpl;

import spoon.smpl.formula.*;

import java.util.HashMap;
import java.util.Map;

/**
 * A MetadataLabel is used to associate a CTL state with an exported arbitrary metadata key-value pair.
 */
public class MetadataLabel implements Label {
    /**
     * Create a new metadata key-value pair label.
     *
     * @param key Metadata key
     * @param value Metadata value
     */
    public MetadataLabel(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Check if a given predicate matches the label, binding the appropriate environment variable
     * on a successful match.
     *
     * @param obj Predicate to test
     * @return True if the predicate is a MetadataPredicate with matching key, false otherwise
     */
    @Override
    public boolean matches(Predicate obj) {
        if (obj instanceof MetadataPredicate && ((MetadataPredicate) obj).getKey().equals(key)) {
            metavarBindings = new HashMap<>();
            metavarBindings.put(((MetadataPredicate) obj).getVarname(), value);
            return true;
        }

        return false;
    }

    /**
     * Retrieve any metavariable bindings involved in matching the most recently given predicate.
     *
     * @return Most recent metavariable bindings, or null if there are none
     */
    @Override
    public Map<String, Object> getMetavariableBindings() {
        return metavarBindings;
    }

    /**
     * Reset/clear metavariable bindings
     */
    @Override
    public void reset() {
        metavarBindings = new HashMap<>();
    }

    @Override
    public String toString() {
        return "Metadata(" + key + ":" + value.toString() + ")";
    }

    /**
     * Exported metadata key.
     */
    private String key;

    /**
     * Exported metadata value.
     */
    private Object value;

    /**
     * The most recently matched metavariable bindings.
     */
    private Map<String, Object> metavarBindings;
}
