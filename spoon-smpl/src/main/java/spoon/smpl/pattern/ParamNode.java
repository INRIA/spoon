package spoon.smpl.pattern;

/**
 * ParamNode is a pattern node that represents the pattern-matching task of binding an arbitrary,
 * structurally-corresponding element found in the "target" pattern. A "target" pattern is a pattern
 * containing only concrete value nodes (no ParamNodes), and it becomes the "target" when we try
 * to match an arbitrary pattern (which may include ParamNodes) against it.
 *
 * The concept of this class roughly corresponds to spoon.pattern.internal.node.ParameterNode
 */
public class ParamNode implements PatternNode {
    /**
     * Create a new ParamNode with a given name.
     *
     * @param name Name of the parameter represented by this node
     */
    public ParamNode(String name) {
        this.name = name;
    }

    /**
     * Visitor pattern dispatch.
     *
     * @param visitor Visitor to accept
     */
    @Override
    public void accept(PatternNodeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Compare this pattern node to a given pattern node.
     *
     * @param other Pattern node to compare against
     * @return True if nodes match, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof ParamNode) {
            return name.equals(((ParamNode) other).name);
        }
        else {
            return false;
        }
    }

    /**
     * Get a String representation of this pattern node.
     *
     * @return String representation
     */
    @Override
    public String toString() {
        return "Param(" + name + ")";
    }

    /**
     * Name of the parameter represented by this node.
     */
    public String name;
}
