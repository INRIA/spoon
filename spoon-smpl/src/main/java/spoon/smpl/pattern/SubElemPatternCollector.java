package spoon.smpl.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * SubElemPatternCollector implements the building of a list of all ElemNodes sub-patterns contained in a
 * given pattern.
 */
public class SubElemPatternCollector implements PatternNodeVisitor {
    /**
     * Create a new SubElemPatternCollector.
     */
    public SubElemPatternCollector() {
        subPatterns = new ArrayList<>();
    }

    /**
     * Get the list of ElemNode sub-patterns.
     *
     * @return List of ElemNode sub-patterns
     */
    public List<ElemNode> getResult() {
        return subPatterns;
    }

    /**
     * Collect an element pattern and all of its element sub-patterns.
     *
     * @param node Pattern to collect
     */
    @Override
    public void visit(ElemNode node) {
        subPatterns.add(node);

        for (String key : node.sub.keySet()) {
            node.sub.get(key).accept(this);
        }
    }

    /**
     * Parameter patterns are ignored.
     *
     * @param node Pattern to collect
     */
    @Override
    public void visit(ParamNode node) {
    }

    /**
     * Value patterns are ignored.
     *
     * @param node Pattern to collect
     */
    @Override
    public void visit(ValueNode node) {
    }

    /**
     * Storage for resulting list of ElemNode sub-patterns.
     */
    private List<ElemNode> subPatterns;
}
