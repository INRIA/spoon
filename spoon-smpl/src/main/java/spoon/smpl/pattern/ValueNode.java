package spoon.smpl.pattern;

/**
 * Part of temporary substitute for spoon.pattern
 *
 * This class roughly corresponds to spoon.pattern.internal.node.ConstantNode
 */
public class ValueNode implements PatternNode {
    public ValueNode(Object value) {
        this.value = value;
    }

    @Override
    public void accept(PatternNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ValueNode) {
            return value.equals(((ValueNode)other).value);
        }
        else {
            return false;
        }
    }

    public Object value;
}
