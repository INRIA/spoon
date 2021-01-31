package spoon.smpl.pattern;

/**
 * ValueNode is a pattern node corresponding to some concrete flat (containing no sub-patterns) value
 * such as a String or an Integer.
 *
 * A ValueNode contains two values; one value to match, and one value to "hold". The idea is that some
 * matching tasks over complex values can be made simpler or more flexible by using a separate arbitrary
 * match value (such as a String), while the original complex value can be stored and retrieved in the
 * form of the "held" value. For simple matching tasks the two values will typically be identical.
 *
 * The concept of this class roughly corresponds to spoon.pattern.internal.node.ConstantNode
 */
public class ValueNode implements PatternNode {
    /**
     * Create a new ValueNode using a given Object to match, and a given Object to hold.
     *
     * @param matchValue Value to match
     * @param heldValue Value to hold
     */
    public ValueNode(Object matchValue, Object heldValue) {
        this.matchValue = matchValue;
        this.heldValue = heldValue;
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
        if (other instanceof ValueNode) {
            return matchValue.equals(((ValueNode)other).matchValue);
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
        StringBuilder sb = new StringBuilder();

        sb.append("Val(");

        if (matchValue == null) {
            sb.append("null");
        } else {
            sb.append(matchValue.getClass().getSimpleName());
            sb.append(":");
            sb.append(matchValue.toString());
        }

        sb.append("; ");

        if (heldValue == null) {
            sb.append("null");
        } else {
            sb.append(heldValue.getClass().getSimpleName());
            sb.append(":");
            sb.append(heldValue.toString());
        }

        sb.append(")");
        return sb.toString();
    }

    /**
     * Value to match.
     */
    public final Object matchValue;

    /**
     * Value to hold.
     */
    public final Object heldValue;
}
