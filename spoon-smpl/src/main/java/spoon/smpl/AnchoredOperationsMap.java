package spoon.smpl;

import spoon.smpl.operation.Operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An AnchoredOperationsMap is a Map from line numbers (integers) to Lists of Operations.
 */
public class AnchoredOperationsMap extends HashMap<Integer, List<Operation>> {
    /**
     * Special key used for anchoring operations to the method body block.
     */
    public static final Integer methodBodyAnchor = -1;

    /**
     * Get operations anchored to the method body block, if any.
     *
     * @return List of operations, or null.
     */
    public List<Operation> getOperationsAnchoredToMethodBody() {
        return getOrDefault(methodBodyAnchor, null);
    }

    /**
     * Ensure a key exists, adding it if it does not.
     *
     * @param k Key which must exist
     */
    public void addKeyIfNotExists(Integer k) {
        if (!containsKey(k)) {
            put(k, new ArrayList<>());
        }
    }

    /**
     * Merge all contents from a second AnchoredOperationsMap map into
     * this one.
     *
     * @param other AnchoredOperationsMap from which to merge all contents
     */
    public void join(AnchoredOperationsMap other) {
        for (int k : other.keySet()) {
            addKeyIfNotExists(k);
            get(k).addAll(other.get(k));
        }
    }
}
