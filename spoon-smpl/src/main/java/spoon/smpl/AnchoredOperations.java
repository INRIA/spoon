package spoon.smpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnchoredOperations extends HashMap<Integer, List<Operation>> {
    public static final Integer methodBodyAnchor = -1;

    public List<Operation> getOperationsAnchoredToMethodBody() {
        return getOrDefault(methodBodyAnchor, null);
    }

    public void addKeyIfNotExists(Integer k) {
        if (!containsKey(k)) {
            put(k, new ArrayList<>());
        }
    }

    public void join(AnchoredOperations other) {
        for (int k : other.keySet()) {
            addKeyIfNotExists(k);
            get(k).addAll(other.get(k));
        }
    }
}
